package org.onexus.resource.api.utils.string;

import java.util.Map;

/**
 * Original code extract from Apache Wicket core *
 */
public class MapVariableInterpolator extends VariableInterpolator
{
    /** Map of variables */
    private Map<?, ?> variables;

    /**
     * Constructor.
     *
     * @param string
     *            a <code>String</code> to interpolate into
     * @param variables
     *            the variables to substitute
     */
    public MapVariableInterpolator(final String string, final Map<?, ?> variables)
    {
        super(string);
        this.variables = variables;
    }

    /**
     * Constructor.
     *
     * @param string
     *            a <code>String</code> to interpolate into
     * @param variables
     *            the variables to substitute
     * @param exceptionOnNullVarValue
     *            if <code>true</code> an {@link IllegalStateException} will be thrown if
     *            {@link #getValue(String)} returns <code>null</code>, otherwise the
     *            <code>${varname}</code> string will be left in the <code>String</code> so that
     *            multiple interpolators can be chained
     */
    public MapVariableInterpolator(final String string, final Map<?, ?> variables,
                                   final boolean exceptionOnNullVarValue)
    {
        super(string, exceptionOnNullVarValue);
        this.variables = variables;
    }

    /**
     * Sets the <code>Map</code> of variables.
     *
     * @param variables
     *            the <code>Map</code> of variables
     */
    public final void setVariables(final Map<?, ?> variables)
    {
        this.variables = variables;
    }

    /**
     * Retrieves a value for a variable name during interpolation.
     *
     * @param variableName
     *            the variable name
     * @return the value
     */
    @Override
    protected String getValue(final String variableName)
    {
        Object value = variables.get(variableName);

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    /**
     * Interpolates a <code>String</code> with the arguments defined in the given <code>Map</code>.
     *
     * @param string
     *            a <code>String</code> to interpolate into
     * @param variables
     *            the variables to substitute
     * @return the interpolated <code>String</code>
     */
    public static String interpolate(final String string, final Map<?, ?> variables)
    {
        return new MapVariableInterpolator(string, variables).toString();
    }

}

