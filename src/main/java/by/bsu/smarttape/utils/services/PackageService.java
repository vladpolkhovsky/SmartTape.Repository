package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.utils.results.PackageStatus;

public interface PackageService {
    /**
     * Позволяет получить пакет по его ID номеру.
     * @param id ID пакета.
     * @return PackageStatus code OK, тогда package содержит пакет полученный из БД. Иначе Error.
     */
    PackageStatus getPackage(long id);

    /**
     * Сохраняет новый пакет в базу данных.
     * @param newPackage - новый пакет
     * @return PackageStatus code OK - поле package будет содержать даннный пакет, но
     * с обновлённым идентифекатором, иначе Error и message будет содержать информацию об ошибке.
     */
    PackageStatus savePackage(by.bsu.smarttape.models.Package newPackage);

    /**
     * Обновление уже существющего пакета.
     * @param updPackage пакет который бует обновлён, важно чтобы поле id было правильным.
     * Это значит что после получения этого пакета из БД. Оно должно быть неизменным.
     * @return PackageStatus code OK - поле package будет содержать обновлённый пакет. Иначе Error.
     */
    PackageStatus updatePackage(by.bsu.smarttape.models.Package updPackage);

    /**
     * Удаление пакета по id.
     * @param id уникальный номер пакета.
     * @return PackageStatus code OK, если удаление прошло умпешно - package будет содержать удалённый пакет. Иначе error.
     */
    PackageStatus deletePackage(long id);

    /**
     * Объединение пакетов.
     * @param packageID
     * @param userID
     * @return
     */
    PackageStatus mergePackage(long packageID, long userID);
}
