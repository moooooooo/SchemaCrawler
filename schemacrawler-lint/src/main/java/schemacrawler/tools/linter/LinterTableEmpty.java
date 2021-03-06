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
package schemacrawler.tools.linter;


import static java.util.Objects.requireNonNull;
import static schemacrawler.utility.QueryUtility.executeForLong;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.utility.Identifiers;
import schemacrawler.utility.Query;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public class LinterTableEmpty
  extends BaseLinter
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(LinterTableEmpty.class.getName());

  public LinterTableEmpty()
  {
    setSeverity(LintSeverity.low);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary()
  {
    return "empty table";
  }

  @Override
  protected void lint(final Table table, final Connection connection)
  {
    requireNonNull(table, "No table provided");
    requireNonNull(connection, "No connection provided");

    final Query query = new Query("Count", "SELECT COUNT(*) FROM ${table}");
    try
    {
      final Identifiers identifiers = Identifiers.identifiers()
        .withConnection(connection).build();
      final long count = executeForLong(query, connection, table, identifiers);
      if (count == 0)
      {
        addTableLint(table, getSummary());
      }
    }
    catch (final SQLException | SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING,
                 new StringFormat("Could not get count for table, ", table),
                 e);
    }
  }

}
