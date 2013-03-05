package org.onexus.resource.api.utils.string;


import java.io.Serializable;

/**
 * Original code extract from Apache Wicket core
 */
public abstract class VariableInterpolator implements Serializable
{
    /** The <code>String</code> to interpolate into */
    protected final String string;

    private final boolean exceptionOnNullVarValue;

    /**
     * Constructor.
     *
     * @param string
     *            a <code>String</code> to interpolate with variable values
     */
    public VariableInterpolator(final String string)
    {
        this(string, false);
    }

    /**
     * Constructor.
     *
     * @param string
     *            a <code>String</code> to interpolate with variable values
     * @param exceptionOnNullVarValue
     *            if <code>true</code> an {@link IllegalStateException} will be thrown if
     *            {@link #getValue(String)} returns <code>null</code>, otherwise the
     *            <code>${varname}</code> string will be left in the <code>String</code> so that
     *            multiple interpolators can be chained
     */
    public VariableInterpolator(final String string, final boolean exceptionOnNullVarValue)
    {
        this.string = string;
        this.exceptionOnNullVarValue = exceptionOnNullVarValue;
    }

    /**
     * Retrieves a value for a variable name during interpolation.
     *
     * @param variableName
     *            a variable name
     * @return the value
     */
    protected abstract String getValue(String variableName);

    private int lowerPositive(final int i1, final int i2)
    {
        if (i2 < 0)
        {
            return i1;
        }
        else if (i1 < 0)
        {
            return i2;
        }
        else
        {
            return i1 < i2 ? i1 : i2;
        }
    }

    /**
     * Interpolates using variables.
     *
     * @return the interpolated <code>String</code>
     */
    @Override
    public String toString()
    {

        if (string == null)
        {
            return null;
        }

        // If there's any reason to go to the expense of property expressions
        if (!string.contains("${"))
        {
            return string;
        }

        // Result buffer
        final StringBuilder buffer = new StringBuilder();

        // For each occurrences of "${"or "$$"
        int start;
        int pos = 0;

        while ((start = lowerPositive(string.indexOf("$$", pos), string.indexOf("${", pos))) != -1)
        {
            // Append text before possible variable
            buffer.append(string.substring(pos, start));

            if (string.charAt(start + 1) == '$')
            {
                buffer.append("$");
                pos = start + 2;
                continue;
            }


            // Position is now where we found the "${"
            pos = start;

            // Get start and end of variable name
            final int startVariableName = start + 2;
            final int endVariableName = string.indexOf('}', startVariableName);

            // Found a close brace?
            if (endVariableName != -1)
            {
                // Get variable name inside brackets
                final String variableName = string.substring(startVariableName, endVariableName);

                // Get value of variable
                final String value = getValue(variableName);

                // If there's no value
                if (value == null)
                {
                    if (exceptionOnNullVarValue)
                    {
                        throw new IllegalArgumentException("Value of variable [[" + variableName +
                                "]] could not be resolved while interpolating [[" + string + "]]");
                    }
                    else
                    {
                        // Leave variable uninterpolated, allowing multiple
                        // interpolators to
                        // do their work on the same string
                        buffer.append("${").append(variableName).append("}");
                    }
                }
                else
                {
                    // Append variable value
                    buffer.append(value);
                }

                // Move past variable
                pos = endVariableName + 1;
            }
            else
            {
                break;
            }
        }

        // Append anything that might be left
        if (pos < string.length())
        {
            buffer.append(string.substring(pos));
        }

        // Convert result to String
        return buffer.toString();
    }
}

