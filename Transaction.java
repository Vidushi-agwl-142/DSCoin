package DSCoinPackage;

public class Transaction {
  public Transaction previous;
  public Transaction next;
  public TransactionBlock parent;
  public String coinID;
  public Members Source;
  public Members Destination;
  public TransactionBlock coinsrc_block;
}
