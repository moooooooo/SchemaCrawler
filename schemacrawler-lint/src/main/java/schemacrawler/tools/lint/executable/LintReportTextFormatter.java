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
package schemacrawler.tools.lint.executable;


import static java.util.Comparator.naturalOrder;
import static sf.util.Utility.isBlank;

import java.io.Serializable;
import java.util.*;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintReport;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.utility.NamedObjectSort;
import sf.util.Color;
import sf.util.Multimap;

final class LintReportTextFormatter
  extends BaseTabularFormatter<LintOptions>
  implements LintReportBuilder
{

  private static final String LINT_KEY = "schemacrawler.lint";

  private static Collection<Lint<?>> getLint(final AttributedObject namedObject)
  {
    if (namedObject == null)
    {
      return null;
    }

    final List<Lint<? extends Serializable>> lints = new ArrayList<>(namedObject
                                                                       .getAttribute(
                                                                         LINT_KEY,
                                                                         new ArrayList<>()));
    lints.sort(naturalOrder());
    return lints;
  }
  private final Catalog catalog;
  private final LintOptions lintOptions;

  LintReportTextFormatter(final Catalog catalog,
                          final LintOptions lintOptions,
                          final OutputOptions outputOptions,
                          final String identifierQuoteString)
    throws SchemaCrawlerException
  {
    super(lintOptions, false, outputOptions, identifierQuoteString);
    this.catalog = catalog;
    this.lintOptions = lintOptions;
  }

  public void generateLintReport(final LintReport report)
    throws SchemaCrawlerException
  {

    this.begin();

    this.handleInfoStart();
    this.handle(catalog.getSchemaCrawlerInfo());
    this.handle(catalog.getDatabaseInfo());
    this.handle(catalog.getJdbcDriverInfo());
    this.handleInfoEnd();

    this.handleStart();
    this.handle(catalog);

    final List<? extends Table> tablesList = new ArrayList<>(catalog
                                                               .getTables());
    Collections.sort(tablesList,
                     NamedObjectSort.getNamedObjectSort(lintOptions
                                                          .isAlphabeticalSortForTables()));
    for (final Table table : tablesList)
    {
      this.handle(table);
    }

    this.handleEnd();

    this.end();
  }

  private void handle(final Catalog catalog)
  {
    final Collection<Lint<?>> lints = getLint(catalog);
    if (lints != null && !lints.isEmpty())
    {
      formattingHelper.writeObjectStart();

      formattingHelper
        .writeObjectNameRow("", "Database", "[database]", Color.white);

      printLints(lints);
      formattingHelper.writeObjectEnd();
    }
  }

  private void handle(final Table table)
  {
    final Collection<Lint<?>> lints = getLint(table);
    if (lints != null && !lints.isEmpty())
    {
      formattingHelper.writeObjectStart();

      formattingHelper.println();
      formattingHelper.println();

      final String tableType = "[" + table.getTableType() + "]";
      formattingHelper.writeObjectNameRow(nodeId(table),
                                          identifiers.quoteFullName(table),
                                          tableType,
                                          colorMap.getColor(table));
      printLints(lints);
      formattingHelper.writeObjectEnd();
    }
  }

  private void handleEnd()
  {
    // No output required
  }

  private void handleStart()
  {
    formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Lints");
  }

  private void printLints(final Collection<Lint<?>> lints)
  {
    formattingHelper.writeEmptyRow();

    final Multimap<LintSeverity, Lint<?>> multiMap = new Multimap<>();
    for (final Lint<?> lint : lints)
    {
      multiMap.add(lint.getSeverity(), lint);
    }
    final List<LintSeverity> severities = Arrays.asList(LintSeverity.values());
    Collections.reverse(severities);
    for (final LintSeverity severity : severities)
    {
      if (!multiMap.containsKey(severity))
      {
        continue;
      }

      formattingHelper.writeNameRow("", String.format("[lint, %s]", severity));
      final List<Lint<?>> lintsById = new ArrayList<>(multiMap.get(severity));
      for (final Lint<?> lint : lintsById)
      {
        final Object lintValue = lint.getValue();
        if (lintValue instanceof Boolean)
        {
          if ((Boolean) lintValue)
          {
            formattingHelper.writeRow("", lint.getMessage(), "");
          }
        }
        else
        {
          formattingHelper
            .writeRow("", lint.getMessage(), lint.getValueAsString());
        }
      }
    }
  }
}
