package rhyme;

import elements.Word;

import java.util.List;

public class Rhyme {

    private int rhymeId;
    private Word model;
    private List<Word> instances;

    public Rhyme(Word model) {
        this.model = model;
    }

    public Rhyme(int rhymeId) {
        this.rhymeId = rhymeId;
    }

    public int getRhymeId() {
        return rhymeId;
    }

    public void setRhymeId(int rhymeId) {
        this.rhymeId = rhymeId;
    }

    public Word getModel() {
        return model;
    }

    public void setModel(Word model) {
        this.model = model;
    }

    public List<Word> getInstances() {
        return instances;
    }

    public void setInstances(List<Word> instances) {
        this.instances = instances;
    }

    public void addInstance(Word instance) {
        this.instances.add(instance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rhyme rhyme = (Rhyme) o;

        return getRhymeId() == rhyme.getRhymeId();
    }

    @Override
    public int hashCode() {
        return getRhymeId();
    }
}
