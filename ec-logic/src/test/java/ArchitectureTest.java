import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;



// ① スキャン対象とするルートパッケージを指定
@AnalyzeClasses(packages = "domain.model.products_inventory")
public class ArchitectureTest {

    // ② アクセス制限ルールを定義
    @ArchTest
    public static final ArchRule 注文集約の内部クラスは外部パッケージからアクセス不可 =
            classes()
                    .that().resideInAPackage("..domain.model.products_inventory..") // 💡 orderパッケージ配下の全クラスを対象に
                    .and().doNotHaveSimpleName("Product")     // 💡 ただし集約ルートである「Order」は除外
                    .should().onlyBeAccessed().byClassesThat()
                    .resideInAPackage("..domain.model.products_inventory..");       // 💡 それらは同じパッケージ内からしかアクセスを許さない

    @ArchTest
    public static final ArchRule restrict_product_get_skus_access = noClasses()
            // 1. 制限の「対象外」にするクラスの条件（マッパーと、Product自身、テストクラスを除外）
            .that().haveSimpleNameNotEndingWith("Mapper")
            .and().haveSimpleNameNotEndingWith("Product")
            .and().haveSimpleNameNotEndingWith("Test")
            .and().haveSimpleNameNotEndingWith("ArchitectureTest")
            // 2. それ以外のクラスが「やってはいけない」禁止事項を定義
            .should().callMethod("com.example.yourproject.domain.Product", "getSkus")
            // 3. エラー発生時に表示するメッセージ（理由）
            .because("Productドメインモデルの getSkus() は、データの詰め替えを担当するMapperクラスからのみ呼び出し可能です。"
                    + "ビジネスロジックは、Product内のメソッド（振る舞い）としてカプセル化してください。");
}



