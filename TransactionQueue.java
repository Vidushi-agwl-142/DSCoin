package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
    transaction.previous = lastTransaction; transaction.next = null;
    if(lastTransaction!=null){
      lastTransaction.next = transaction; lastTransaction = transaction;
    }
    else{
      firstTransaction=transaction; lastTransaction = transaction;
    }
    numTransactions ++;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(firstTransaction!=null){
      Transaction rem = firstTransaction;
      numTransactions--;
      if(firstTransaction==lastTransaction){
        firstTransaction = null;
        lastTransaction = null;
      }
      else{
        firstTransaction = firstTransaction.next;
        firstTransaction.previous = null;
      }
      return rem;
    }
    else {throw new EmptyQueueException();}
  }

  public int size() {
    return numTransactions;
  }
}
