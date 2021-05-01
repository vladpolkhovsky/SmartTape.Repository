package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.models.Link;
import by.bsu.smarttape.models.Package;
import by.bsu.smarttape.utils.results.PackageStatus;
import by.bsu.smarttape.utils.results.SimpleStatus;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BasicPackageService implements PackageService {

    public static final long NON_LOGON_PACKAGE = 0;

    private static PackageService service = null;

    private BasicPackageService() {

    }

    public static PackageService getInstance() {
        if (service == null)
            service = new BasicPackageService();
        return service;
    }

    @Override
    public PackageStatus getPackage(long id) {
        AtomicInteger code = new AtomicInteger(SimpleStatus.OK);
        AtomicReference<Package> aPackage = new AtomicReference<>(null);
        AtomicReference<String> message = new AtomicReference<>("OK");

        Exception exception = DataBaseSessionService.safetyOperation(session -> {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Package> criteriaQuery = criteriaBuilder.createQuery(Package.class);
            Root<Package> packageRoot = criteriaQuery.from(Package.class);
            criteriaQuery.select(packageRoot)
                .where(criteriaBuilder.equal(packageRoot.get("id"), id));
            TypedQuery<Package> query = session.createQuery(criteriaQuery);
            List<Package> list = query.getResultList();
            if (list.size() != 1) {
                code.set(SimpleStatus.ERROR);
                if (list.size() == 0)
                    message.set("Пакета с таким ID не найдено.");
                else
                    message.set("Ошибка сервера. Пакетов с ID=" + id + " > 1.");
            } else {
                aPackage.set(list.get(0));
            }
        });

        if (exception != null) {
            code.set(SimpleStatus.ERROR);
            message.set(exception.toString());
            aPackage.set(null);
        } else {
            exception = DataBaseSessionService.safetyOperation(session -> {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Link> criteriaQuery = criteriaBuilder.createQuery(Link.class);
                Root<Link> linkRoot = criteriaQuery.from(Link.class);
                criteriaQuery.select(linkRoot)
                        .where(criteriaBuilder.equal(linkRoot.get("packageId"), aPackage.get().getId()));
                TypedQuery<Link> query = session.createQuery(criteriaQuery);
                aPackage.get().setLinks(query.getResultList());
            });
            if (exception != null) {
                code.set(SimpleStatus.ERROR);
                message.set("Ошибка при получении ссылок");
                aPackage.set(null);
            }
        }

        return PackageStatus.createStatus(
                code.get(),
                message.get(),
                aPackage.get()
        );
    }

    // НЕ УВЕРЕН ЧТО РАБОТАЕТ СТАБИЛЬНО.
    @Override
    public PackageStatus savePackage(Package newPackage) {
        Exception exception = DataBaseSessionService.safetyOperation(session -> {
            session.save(newPackage);
            long pId = newPackage.getId();
            newPackage.getLinks().forEach((x) -> {
                x.setPackageId(pId);
                session.save(x);
            });
        });
        if(exception == null) {
            PackageStatus status = getPackage(newPackage.getId());
            if (status.getCode() == SimpleStatus.OK)
                return PackageStatus.createStatus(
                        SimpleStatus.OK,
                        "OK",
                        status.getPackage()
                );
            return PackageStatus.createStatus(
                    SimpleStatus.ERROR,
                    "Во время загрузки пакета произошла ошибка.",
                    null
            );
        }
        return PackageStatus.createStatus(SimpleStatus.ERROR, exception.getMessage(), null);
    }

    /**
     * @param updPackage пакет который бует обновлён, важно чтобы поле id было правильным.
     * Это значит что после получения этого пакета из БД. Оно должно быть неизменным.
     * Важно отметить, что ссылки которые были изменены должны иметь тот же самый ID, добавленные
     * ссылки должны иметь ID = 0, а ссылки которые были удалены должны иметь ID < 0.
     * @return PackageStatus который будет иметь обновлённый пакет, если всё прошло успешно или сообщение об ошибке.
     */

    @Override
    public PackageStatus updatePackage(Package updPackage) {
        Exception ex = DataBaseSessionService.safetyOperation(session -> {
            session.evict(updPackage);
            session.update(updPackage);
           for (Link link : updPackage.getLinks()) {
               if (link.getId() < 0)
                   delLink(session, link);
               else if (link.getId() == 0)
                   addLink(session, link);
               else if (link.getId() > 0)
                   updLink(session, link);
           }
        });
        if (ex != null) {
            return new PackageStatus(
                    SimpleStatus.ERROR,
                    ex.getMessage(),
                    null
            );
        }

        return new PackageStatus(
                SimpleStatus.OK,
                null,
                BasicPackageService.getInstance().getPackage(updPackage.getId()).getPackage()
        );
    }

    private void updLink(Session session, Link link) {
        session.evict(link);
        session.update(link);
    }

    private void delLink(Session session, Link link) {
        link.setId(-1 * link.getId());
        session.delete(link);
    }

    private void addLink(Session session, Link link) {
        session.save(link);
        long pId = link.getId();
        link.setId(pId);
    }

    @Override
    public PackageStatus deletePackage(long id) {
        return null;
    }

    @Override
    public PackageStatus mergePackage(long packageID, long userID) {
        return null;
    }
}
