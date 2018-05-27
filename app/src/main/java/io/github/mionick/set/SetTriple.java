package io.github.mionick.set;

/**
 * Created by Nick on 5/27/2018.
 */

public class SetTriple {
    private SetCard card1;
    private SetCard card2;
    private SetCard card3;

    public SetTriple(SetCard card1, SetCard card2, SetCard card3) {

        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
    }

    public SetCard[] toArray() {
        return new SetCard[]{card1, card2, card3};
    }

    public SetCard getCard1() {
        return card1;
    }

    public void setCard1(SetCard card1) {
        this.card1 = card1;
    }

    public SetCard getCard2() {
        return card2;
    }

    public void setCard2(SetCard card2) {
        this.card2 = card2;
    }

    public SetCard getCard3() {
        return card3;
    }

    public void setCard3(SetCard card3) {
        this.card3 = card3;
    }
}
