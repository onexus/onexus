/**
 *  Copyright 2012 Universitat Pompeu Fabra.
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
package org.onexus.website.widgets.tableviewer.decorators.ct;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.website.widgets.tableviewer.decorators.utils.FieldDecorator;

import java.util.HashMap;
import java.util.Map;

public class CtDecorator extends FieldDecorator {

    private static final String EMPTY_COLOR = "#FFFFFF";

    public static final Map<String, String> COLORS = new HashMap<String, String>();

    static {

        // NSSNP - #FFCCE6
        COLORS.put("NON_SYNONYMOUS_CODING", "#FFCCE6");

        // RSNP - #002EB8 (#cdddff)
        COLORS.put("REGULATORY_REGION", "#cdddff");
        COLORS.put("DOWNSTREAM", "#cdddff");
        COLORS.put("UPSTREAM", "#cdddff");

        // SSNP - #F5B800
        COLORS.put("SYNONYMOUS_CODING", "#F5B800");

        // STOP_SNP - #CCFF33
        COLORS.put("STOP_GAINED", "#CCFF33");
        COLORS.put("STOP_LOST", "#CCFF33");

        // SPLICE_SNP - #FF6633
        COLORS.put("SPLICE_SITE", "#FF6633");
        COLORS.put("ESSENTIAL_SPLICE_SITE", "#FF6633");

        // OTRSNP - #FFFFCC
        COLORS.put("3PRIME_UTR", "#FFFFCC");
        COLORS.put("WITHIN_NON_CODING_GENE", "#FFFFCC");
        COLORS.put("NMD_TRANSCRIPT", "#FFFFCC");
        COLORS.put("5PRIME_UTR", "#FFFFCC");

        COLORS.put("INTRONIC", "#FFFFCC");
        COLORS.put("INTERGENIC", "#FFFFCC");
        COLORS.put("FRAMESHIFT_CODING", "#FFFFCC");
        COLORS.put("WITHIN_MATURE_MIRNA", "#FFFFCC");

    }

    public static final Map<String, String> SHORT_NAMES = new HashMap<String, String>();

    static {
        SHORT_NAMES.put("3PRIME_UTR", "3-UTR");
        SHORT_NAMES.put("NON_SYNONYMOUS_CODING", "NS-SNP");
        SHORT_NAMES.put("SYNONYMOUS_CODING", "S-SNP");
        SHORT_NAMES.put("WITHIN_NON_CODING_GENE", "non-Cod");
        SHORT_NAMES.put("STOP_GAINED", "stp-gain");
        SHORT_NAMES.put("STOP_LOST", "stp-lost");
        SHORT_NAMES.put("NMD_TRANSCRIPT", "NMD");
        SHORT_NAMES.put("SPLICE_SITE", "splice");
        SHORT_NAMES.put("5PRIME_UTR", "5-UTR");
        SHORT_NAMES.put("DOWNSTREAM", "downst");
        SHORT_NAMES.put("REGULATORY_REGION", "Reg-SNP");
        SHORT_NAMES.put("ESSENTIAL_SPLICE_SITE", "e-splice");
        SHORT_NAMES.put("UPSTREAM", "upst");
        SHORT_NAMES.put("INTRONIC", "intro");
        SHORT_NAMES.put("INTERGENIC", "intergen");
        SHORT_NAMES.put("FRAMESHIFT_CODING", "frameshift");
        SHORT_NAMES.put("WITHIN_MATURE_MIRNA", "mature miRNA");
        SHORT_NAMES.put("NONCODING_RNA", "non-rna");

    }

    private String cssClass;

    public CtDecorator(Field valueProperty, String cssClass) {
        super(valueProperty);
        this.cssClass = cssClass;
    }

    protected String getTooltip(IEntity data) {
        Object value = data.get(getField().getId());
        return value == null ? "" : value.toString();
    }

    @Override
    public String getColor(IEntity data) {
        Object value = getValue(data);

        return value == null ? EMPTY_COLOR : COLORS.get(value.toString().toUpperCase());
    }

    @Override
    public String getTemplate() {
        return null;
    }

    @Override
    public void setTemplate(String template) {

    }

    @Override
    public void populateCell(WebMarkupContainer cellContainer,
                             String componentId, IModel<IEntity> data) {
        IEntity entity = data.getObject();

        Object value = entity != null ? entity.get(getField().getId()) : null;
        String shortName = value != null ? SHORT_NAMES.get(value.toString().toUpperCase()) : "";
        shortName = shortName == null ? value.toString().toLowerCase() : shortName;
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
