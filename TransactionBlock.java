package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    trarray = new Transaction[t.length];
    for(int i=0 ; i<t.length ; i++){
      t[i].parent=this;
      trarray[i] = t[i];
    }
    previous = null;
    dgst = null;
    Tree = new MerkleTree();
    Tree.Build(trarray);
    trsummary = Tree.rootnode.val;
    nonce = null;
  }

  public boolean checkTransaction (Transaction t) {
    if(t.coinsrc_block==null){
      return true;
    }
    else{
      Transaction[] src = t.coinsrc_block.trarray;
      boolean found = false;
      for (int i=0 ; i<src.length && !found ; i++){
        if (src[i].coinID.equals(t.coinID) && src[i].Destination.UID.equals(t.Source.UID)){
          found = true;
        }
      }
      if(!found){
//        System.out.println(t.Source.UID);
//        System.out.println("a");
        System.out.println("i");

        return false;
      }
      TransactionBlock curr = this; int var = 0;
      while(curr!=t.coinsrc_block && curr!=null){
        for(Transaction tr : curr.trarray){
          if(tr.coinID==t.coinID){
            if(curr!=this){
              return false;
            }
            else{
              if(t.parent==this){
                if(var>0){
                  return false;
                }
                else{
                  var++;
                }
              }
              else{
//                System.out.println(tr.Source.UID + tr.Destination.UID);
//                System.out.println(t.coinID + t.Destination.UID);
                System.out.println("h");

                return false;
              }
            }
          }
//          if(curr!=this && tr.coinID==t.coinID){
//            System.out.println(t.Source.UID);
//            System.out.println("b");
//            return false;
//          }
//          else if(curr==this && tr.coinID==t.coinID){
//            if(t.parent==null){
//              System.out.println(t.Source.UID);
//              System.out.println("c");
//              return false;
//            }
//            else{
//              if(var>0){
//                System.out.println(t.Source.UID);
//                System.out.println("d");
//                return false;
//              }
//              else{
//                var++;
//              }
//            }
//          }
        }
        curr=curr.previous;
      }
      if(curr==null){
//        System.out.println(t.Source.UID);
//        System.out.println("e");
        return false;
      }
      else{
        return true;
      }
    }
  }
}
