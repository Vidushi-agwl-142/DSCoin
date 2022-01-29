package DSCoinPackage;
import HelperClasses.Pair;
public class Moderator{


  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
      int id = 100000;
      int num_tr = DSObj.bChain.tr_count;
      int latest = 99999+coinCount;
      DSObj.latestCoinID = Integer.toString(latest);
      Transaction[] initarr = new Transaction[coinCount];
      Transaction t ;
      Members[] membarr = DSObj.memberlist; int mems = membarr.length;
      for(int i=0 ; i<coinCount ; ++i){
          t = new Transaction();
          t.coinID = Integer.toString(id);
          Members mod = new Members(); mod.UID="Moderator";
          t.Source=mod;
          t.Destination=membarr[i%mems];
          t.coinsrc_block=null;
          initarr[i]=t;
          id++;
      }
      TransactionBlock tB; int k = 0; int l=0;
      Transaction[] arr;
      for(int j=0 ; j<coinCount/num_tr ; ++j){
          arr = new Transaction[num_tr];
          while(k>=j*num_tr && k<(j+1)*num_tr){
              arr[k%num_tr]=initarr[k];
              k++;
          }
          tB = new TransactionBlock(arr);
          DSObj.bChain.InsertBlock_Honest(tB);
          while(l>=j*num_tr && l<(j+1)*num_tr){
              Pair<String,TransactionBlock> p = new Pair<>(initarr[l].coinID, tB);
              membarr[l%mems].mycoins.add(p);
              l++;
          }
      }
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
      int id = 100000;
      int num_tr = DSObj.bChain.tr_count;
      int latest = 99999+coinCount;
      DSObj.latestCoinID = Integer.toString(latest);
      Transaction[] initarr = new Transaction[coinCount];
      Transaction t ;
      Members[] membarr = DSObj.memberlist; int mems = membarr.length;
      for(int i=0 ; i<coinCount ; ++i){
          t = new Transaction();
          t.coinID = Integer.toString(id);
          Members mod = new Members(); mod.UID="Moderator";
          t.Source=mod;
          t.Destination=membarr[i%mems];
          t.coinsrc_block=null;
          initarr[i]=t;
          id++;
      }
      TransactionBlock tB; int k = 0; int l=0;
      DSObj.bChain.lastBlocksList = new TransactionBlock[100]; DSObj.bChain.lastnotnull=-1;
      Transaction[] arr;
      for(int j=0 ; j<coinCount/num_tr ; ++j){
          arr = new Transaction[num_tr];
          while(k>=j*num_tr && k<(j+1)*num_tr){
              arr[k%num_tr]=initarr[k];
              k++;
          }
          tB = new TransactionBlock(arr);
          DSObj.bChain.InsertBlock_Malicious(tB);
          while(l>=j*num_tr && l<(j+1)*num_tr){
              Pair<String,TransactionBlock> p = new Pair<>(initarr[l].coinID, tB);
              membarr[l%mems].mycoins.add(p);
              l++;
          }
      }

  }
}
