package org.onexus.website.api.pages;

import org.onexus.website.api.pages.browser.IEntitySelection;

import java.io.Serializable;
import java.util.List;

public interface ISelectionContributor extends Serializable {

    List<IEntitySelection> getEntitySelections();

}
