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
package schemacrawler.tools.integration.serialize;


import static sf.util.Utility.isBlank;

import java.util.List;
import java.util.logging.Level;

import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public enum SerializationFormat
  implements OutputFormat
{
  java("Java serialization",
       "schemacrawler.tools.integration.serialize.JavaSerializedCatalog",
       "ser"),
  json("JavaScript Object Notation (JSON) serialization format",
       "schemacrawler.tools.integration.serialize.JsonSerializedCatalog",
       "json"),
  yaml("YAML Ain't Markup Language (YAML) serialization format",
       "schemacrawler.tools.integration.serialize.YamlSerializedCatalog",
       "yaml");

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SerializationFormat.class.getName());

  /**
   * Gets the value from the format.
   *
   * @param format Text output format.
   * @return SerializationFormat
   */
  public static SerializationFormat fromFormat(final String format)
  {
    final SerializationFormat outputFormat = fromFormatOrNull(format);
    if (outputFormat == null)
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Unknown format <%s>, using default",
                                  format));
      return java;
    }
    else
    {
      return outputFormat;
    }
  }

  private static SerializationFormat fromFormatOrNull(final String format)
  {
    if (isBlank(format))
    {
      return null;
    }
    for (final SerializationFormat outputFormat : SerializationFormat.values())
    {
      if (outputFormat.outputFormatState.isSupportedFormat(format))
      {
        return outputFormat;
      }
    }
    return null;
  }

  /**
   * Checks if the value of the format is supported.
   *
   * @return True if the format is a text output format
   */
  public static boolean isSupportedFormat(final String format)
  {
    return fromFormatOrNull(format) != null;
  }

  private final OutputFormatState outputFormatState;
  private final String serializerClassName;

  private SerializationFormat(final String description,
                              final String serializerClassName,
                              final String... additionalFormatSpecifiers)
  {
    outputFormatState = new OutputFormatState(name(),
                                              description,
                                              additionalFormatSpecifiers);
    this.serializerClassName = serializerClassName;
  }

  public String getFileExtension()
  {
    final List<String> formats = outputFormatState.getFormats();
    return formats.get(formats.size() - 1);
  }

  public String getSerializerClassName()
  {
    return serializerClassName;
  }

  @Override
  public String getDescription()
  {
    return outputFormatState.getDescription();
  }

  @Override
  public String getFormat()
  {
    return outputFormatState.getFormat();
  }

  @Override
  public List<String> getFormats()
  {
    return outputFormatState.getFormats();
  }

  @Override
  public String toString()
  {
    return outputFormatState.toString();
  }

}
