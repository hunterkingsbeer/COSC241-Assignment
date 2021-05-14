package week11;

public interface CardPile {

  /*
     Loads a copy of the given array as the pile of cards.
  */
  public void load(int[] cards);

  /*
     Creates a pile of n cards numbered (top to bottom) from 1 to n.
  */
  public void load(int n);

  /*
     Returns a copy of the pile of cards.
  */
  public int[] getPile();

  /*
     Transforms the pile of cards given a row length and a specification
     for picking them up. See assignment details for required behaviour
     if the number of cards is not a multiple of the given row length or
     the specification is invalid.
  */
  public void transform(int rowLength, String spec);

  /*
     Returns the minimum positive number of times we would need to call
     transform(rowLength, spec) on the current pile and return it to its
     original order.
  */
  public int count(int rowLength, String spec);


}
