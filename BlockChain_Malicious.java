package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;
  public int lastnotnull;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    CRF obj = new CRF(64); MerkleTree m = new MerkleTree(); m.Build(tB.trarray);
    if(!tB.dgst.substring(0,4).equals("0000")){return false;}
    else if(tB.previous==null && !tB.dgst.equals(obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce))){return false;}
    else if(tB.previous!=null && !tB.dgst.equals(obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce))){return false;}
    else if(!tB.trsummary.equals(m.rootnode.val)){return false;}
    else {
      for(int i=0 ; i<tB.trarray.length ; i++){
        if(tB.checkTransaction(tB.trarray[i])==false){return false;}
      }
    }
    return true;
  }

  public TransactionBlock FindLongestValidChain() {
    int longest = 0; TransactionBlock retn = lastBlocksList[0];
    for (int i=0 ; lastBlocksList[i]!=null ; i++){
      TransactionBlock curr = lastBlocksList[i]; 
      TransactionBlock last = lastBlocksList[i];
      int j = 0;
      while (curr!=null){
        if(checkTransactionBlock(curr)){j++; curr = curr.previous;}
        else{j=0; last = curr.previous; curr=curr.previous;}
      }
      if(j>longest){longest=j; retn = last;}
    }
    return retn;
  }

  public void InsertBlock_Malicious(TransactionBlock newBlock) {
    if(lastBlocksList[0]==null){
      lastnotnull=-1;
    }
    newBlock.previous = FindLongestValidChain();
    CRF obj = new CRF(64);
    boolean found = false; int i = 1000000001; String s = new String();
    while (!found){
      s = Integer.toString(i);
      if(newBlock.previous==null){
        if(obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s).substring(0,4).equals("0000")){
          found = true;
        }
        else{
          i++;
        }
      }
      else{
        if(obj.Fn(newBlock.previous.dgst + "#" + newBlock.trsummary + "#" + s).substring(0,4).equals("0000")){
          found = true;
        }
        else{
          i++;
        }
      }
    }
    newBlock.nonce = s;
    if(newBlock.previous!=null){
      newBlock.dgst = obj.Fn(newBlock.previous.dgst + "#" + newBlock.trsummary + "#" + s);
    }
    else{
      newBlock.dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + s);
    }
    int j=0; boolean found0 = false;
    while(lastBlocksList[j]!=null && !found0){
      if (lastBlocksList[j]==FindLongestValidChain()){
        found0 = true;
      }
      else {j++;}
    }
    if(found0){
      lastBlocksList[j]=newBlock;
    }
    else {
      lastBlocksList[lastnotnull + 1] = newBlock;
      lastnotnull++;
    }
  }
}
