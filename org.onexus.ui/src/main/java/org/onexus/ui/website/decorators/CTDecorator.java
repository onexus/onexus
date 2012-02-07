/**
 *  Copyright 2011 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.ui.website.decorators;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.IEntity;
import org.onexus.core.resources.Field;

import java.util.HashMap;
import java.util.Map;

public class CTDecorator extends FieldDecorator {

    private static final String emptyColor = "#FFFFFF";

    public static final Map<String, String> colors = new HashMap<String, String>();

    static {

        // NSSNP - #FFCCE6
        colors.put("NON_SYNONYMOUS_CODING", "#FFCCE6");

        // RSNP - #002EB8 (#cdddff)
        colors.put("REGULATORY_REGION", "#cdddff");
        colors.put("DOWNSTREAM", "#cdddff");
        colors.put("UPSTREAM", "#cdddff");

        // SSNP - #F5B800
        colors.put("SYNONYMOUS_CODING", "#F5B800");

        // STOP_SNP - #CCFF33
        colors.put("STOP_GAINED", "#CCFF33");
        colors.put("STOP_LOST", "#CCFF33");

        // SPLICE_SNP - #FF6633
        colors.put("SPLICE_SITE", "#FF6633");
        colors.put("ESSENTIAL_SPLICE_SITE", "#FF6633");

        // OTRSNP - #FFFFCC
        colors.put("3PRIME_UTR", "#FFFFCC");
        colors.put("WITHIN_NON_CODING_GENE", "#FFFFCC");
        colors.put("NMD_TRANSCRIPT", "#FFFFCC");
        colors.put("5PRIME_UTR", "#FFFFCC");

        colors.put("INTRONIC", "#FFFFCC");
        colors.put("INTERGENIC", "#FFFFCC");
        colors.put("FRAMESHIFT_CODING", "#FFFFCC");
        colors.put("WITHIN_MATURE_MIRNA", "#FFFFCC");

    }

    public static final Map<String, String> shortNames = new HashMap<String, String>();

    static {
        shortNames.put("3PRIME_UTR", "3-UTR");
        shortNames.put("NON_SYNONYMOUS_CODING", "NS-SNP");
        shortNames.put("SYNONYMOUS_CODING", "S-SNP");
        shortNames.put("WITHIN_NON_CODING_GENE", "non-Cod");
        shortNames.put("STOP_GAINED", "stp-gain");
        shortNames.put("STOP_LOST", "stp-lost");
        shortNames.put("NMD_TRANSCRIPT", "NMD");
        shortNames.put("SPLICE_SITE", "splice");
        shortNames.put("5PRIME_UTR", "5-UTR");
        shortNames.put("DOWNSTREAM", "downst");
        shortNames.put("REGULATORY_REGION", "Reg-SNP");
        shortNames.put("ESSENTIAL_SPLICE_SITE", "e-splice");
        shortNames.put("UPSTREAM", "upst");
        shortNames.put("INTRONIC", "intro");
        shortNames.put("INTERGENIC", "intergen");
        shortNames.put("FRAMESHIFT_CODING", "frameshift");
        shortNames.put("WITHIN_MATURE_MIRNA", "mature miRNA");
        shortNames.put("NONCODING_RNA", "non-rna");

    }

    private String cssClass;

    public CTDecorator(Field valueProperty, String cssClass) {
        super(valueProperty);
        this.cssClass = cssClass;
    }

    protected String getTooltip(IEntity data) {
        Object value = data.get(getValueProperty().getName());
        return (value == null ? "" : value.toString());
    }

    @Override
    public String getColor(IEntity data) {
        Object value = getValue(data);

        return (value == null ? emptyColor : colors.get(value.toString()
                .toUpperCase()));
    }

    @Override
    public void populateCell(WebMarkupContainer cellContainer,
                             String componentId, IModel<IEntity> data) {
        IEntity entity = data.getObject();

        Object value = (entity != null ? entity.get(getValueProperty()
                .getName()) : null);
        String shortName = (value != null ? shortNames.get(value.toString()
                .toUpperCase()) : "");
        shortName = (shortName == null ? value.toString().toLowerCase()
                : shortName);
        Label empty = new Label(componentId, shortName);
        // empty.setVisible(false);

        cellContainer.add(empty);
        cellContainer.add(new AttributeModifier("style", new Model<String>(
                "background-color: " + getColor(data.getObject()) + ";")));
        cellContainer.add(new AttributeModifier("title", new Model<String>(
                getTooltip(data.getObject()))));
        if (cssClass != null) {
            cellContainer.add(new AttributeModifier("class", new Model<String>(
                    cssClass)));
        }

    }

}
