
package com.fullpower.changeit.service500px;

import java.util.ArrayList;
import java.util.List;
public class Feed500px {

    public int current_page;
    public int total_pages;
    public int total_items;
    public List<Photo500px> photos = new ArrayList<Photo500px>();
    public Filters filters;
    public String feature;

}
