package rhyme;

public class ConsonantPhoneme extends Phoneme {

    public ConsonantPhoneme(PhonemeEnum phonemeEnum) {
        super(phonemeEnum);
        if (super.phonemeEnum.isVowel())
            super.phonemeEnum = null;
    }

    public MannerOfArticulation getManner() {
        return phonemeEnum.getManner();
    }

    public PlaceOfArticulation getPlace() {
        return phonemeEnum.getPlace();
    }

}
