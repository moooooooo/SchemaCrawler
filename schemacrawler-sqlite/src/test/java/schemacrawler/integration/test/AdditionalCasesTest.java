/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.integration.test;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.*;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.*;

@ExtendWith(TestLoggingExtension.class)
@ExtendWith(TestContextParameterResolver.class)
public class AdditionalCasesTest
    extends BaseSqliteTest
{

  @Test
  public void advancedUsage(final TestContext testContext)
      throws Exception
  {
    run(testContext.testMethodFullName(), "/advanced_usage.sql", "details");
  }

  @Test
  public void quotedCreateScript(final TestContext testContext)
      throws Exception
  {
    run(testContext.testMethodFullName(),
        "/identifiers_unquoted.sql",
        "schema");
    run(testContext.testMethodFullName(), "/identifiers_quoted.sql", "schema");
  }

  private void run(final String currentMethodFullName,
                   final String databaseSqlResource,
                   final String command)
      throws Exception
  {
    final Path sqliteDbFile = createTestDatabase(databaseSqlResource);

    final SchemaCrawlerOptions options = DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final SchemaTextOptions textOptions = SchemaTextOptionsBuilder
        .newSchemaTextOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(
        command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder
                                              .builder(textOptions).toConfig());

    assertThat(outputOf(executableExecution(createConnection(sqliteDbFile),
                                            executable)),
               hasSameContentAs(classpathResource(currentMethodFullName)));
  }

}
