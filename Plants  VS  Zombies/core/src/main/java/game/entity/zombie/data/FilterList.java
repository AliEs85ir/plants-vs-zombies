package game.entity.zombie.data;

import app.enums.zombie.FilterListType;

import java.util.List;

public class FilterList {
    private List<String> list;
    private FilterListType listType;    // >>> "include list" or "exclude list"
    private String blockedPlantingReason; // >>> for arcade machine


    public List<String> getList() { return list; }

    public void setList(List<String> list) { this.list = list; }

    public FilterListType getListType() { return listType; }
    public void setListType(FilterListType listType) { this.listType = listType; }

    // >>> Helper method to easily check if a plant is allowed for zombie's ability:

    public boolean contains(String targetId) {

        boolean inList = list != null && list.contains(targetId);

        if (listType == FilterListType.INCLUDE)
        {
            return inList;
        }
        else if (listType == FilterListType.EXCLUDE)
        {
            return !inList;
        }
        return false;
    }
}
