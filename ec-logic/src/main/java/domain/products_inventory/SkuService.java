// SkuService.java
package domain.products_inventory;

/**
 * UI層が呼び出すユースケース（機能）の境界を定義するインターフェース。
 */
public interface SkuService {

    /**
     * 指定された数量の在庫を引き当てます。
     *
     * @param id SKUのID
     * @param quantity 引き当てる数量
     * @return 引き当て後の最新状態を持つDTO
     */
    SkuDto allocate(String id, int quantity);
}
