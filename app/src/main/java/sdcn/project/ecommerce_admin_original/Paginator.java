package sdcn.project.ecommerce_admin_original;

import java.util.ArrayList;

public class Paginator {
    public int TOTAL_NUM_ITEMS;
    public int ITEMS_PER_PAGE;

    public  int ITEMS_REMAINING;
    public  int LAST_PAGE;

    Paginator(int TOTAL_NUM_ITEMS, int ITEMS_PER_PAGE)
    {
        this.TOTAL_NUM_ITEMS=TOTAL_NUM_ITEMS;
        this.ITEMS_PER_PAGE=ITEMS_PER_PAGE;

        this.ITEMS_REMAINING=TOTAL_NUM_ITEMS % ITEMS_PER_PAGE;
        this.LAST_PAGE=TOTAL_NUM_ITEMS/ITEMS_PER_PAGE;
    }

    public ArrayList<String> generatePage(int currentPage )
    {
        int startItem=currentPage*ITEMS_PER_PAGE;
        int numOfData=ITEMS_PER_PAGE;
        // "pageData" array is initialize each time "generatePage" method is called.
        ArrayList<String> pageData=new ArrayList<>();
        // if there are items remaining then those will be show in a new ("extra page")
        if (currentPage==LAST_PAGE && ITEMS_REMAINING>0)
        {
            for (int i=startItem;i<startItem+ITEMS_REMAINING;i++)
            {
                pageData.add("Number "+i);
            }
        }else
        {
            for (int i=startItem;i<startItem+numOfData;i++)
            {
                pageData.add("Number "+i);
            }
        }
        return pageData;
    }

}