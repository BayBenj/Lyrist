package bookofmormon;

import filters.Direction;
import filters.VocabListFilter;
import song.VocabList;

import java.util.*;

public class BomGeographyFilter extends VocabListFilter {

    private static String[] list = {
            "city",
            "plains",
            "wilderness",
            "hill",
            "land",
            "mount",
            "garden",
            "waters",
            "place",
            "forest",
            "river",
            "valley"

    };
    private static Set set = new HashSet(Arrays.asList(list));

    public BomGeographyFilter() {
        super(new VocabList(set, "book of mormon geography"));
    }

    public BomGeographyFilter(Direction direction) {
        super(direction, new VocabList(set, "book of mormon geography"));
    }

    @Override
    public Set<String> doFilter(Set<String> originalStrings) {
        Set<String> result = new HashSet<>();
        for (String s : originalStrings) {
            if (super.getDirection() == Direction.INCLUDE_MATCH && super.vocabList.contains(s.toLowerCase()) ||
                    super.getDirection() == Direction.EXCLUDE_MATCH && !super.vocabList.contains(s.toLowerCase()))
                result.add(s);
        }
        return result;
    }

    public static String[] getList() {
        return list;
    }
}















































































