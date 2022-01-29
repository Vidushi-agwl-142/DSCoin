package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    boolean found = false; int i = 1000000001; String s = new String();
    while (!found){
      s = Integer.toString(i);
      if(lastBlock==null){
        if(obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s).substring(0,4).equals("0000")){
          found = true;
        }
        else{
          i++;
        }
      }
      else if(lastBlock!=null){
        if(obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + s).substring(0,4).equals("0000")){
          found = true;
        }
        else{
          i++;
        }
      }
    }
    newBlock.nonce = s;
    if(lastBlock!=null){
      newBlock.dgst = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + s);
      newBlock.previous = lastBlock;
    }
    else{
      newBlock.dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
      newBlock.previous = null;
    }
    lastBlock = newBlock;
  }
}
