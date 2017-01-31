package songtools;

import constraints.WordConstraint;
import constraints.WordConstraintManager;
import utils.U;
import word2vec.W2vCommander;

import java.util.List;

public class NormalReplacementInfo extends ReplacementByAnalogyInfo {

    public NormalReplacementInfo(List<WordConstraint> markingConstraints, List<WordConstraint> wordConstraints, W2vCommander w2v, double replacementFrequency, String oldTheme, String newTheme) {
        super(markingConstraints, wordConstraints, w2v, replacementFrequency, oldTheme, newTheme);
    }

    public static NormalReplacementInfo getExample() {
            return new NormalReplacementInfo(WordConstraintManager.getMarking(),
                                             WordConstraintManager.getNormal(),
                                             U.getW2vCommander(),
                                            1,
                                            "sorrow",
                                            "terrorism");
    }
}
