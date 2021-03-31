package software.wings.graphql.datafetcher.budget;

import static io.harness.annotations.dev.HarnessTeam.CE;

import io.harness.annotations.dev.HarnessModule;
import io.harness.annotations.dev.OwnedBy;
import io.harness.annotations.dev.TargetModule;

@TargetModule(HarnessModule._375_CE_GRAPHQL)
@OwnedBy(CE)
public class BudgetDefaultKeys {
  private BudgetDefaultKeys() {}

  public static final double ACTUAL_COST = 0.0;
  public static final long TIME = 0L;
  public static final double BUDGET_VARIANCE = 0.0;
  public static final double BUDGET_VARIANCE_PERCENTAGE = 0.0;
}
