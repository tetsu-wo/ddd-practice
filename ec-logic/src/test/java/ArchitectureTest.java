import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;


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
}

