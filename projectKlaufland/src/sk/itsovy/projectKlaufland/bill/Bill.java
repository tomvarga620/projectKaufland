package sk.itsovy.projectKlaufland.bill;

import sk.itsovy.projectKlaufland.Exepction.BillException;
import sk.itsovy.projectKlaufland.database.Database;
import sk.itsovy.projectKlaufland.items.Item;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sk.itsovy.projectKlaufland.items.Piece;
import sk.itsovy.projectKlaufland.items.drink.DraftInterface;
import sk.itsovy.projectKlaufland.items.food.Fruit;
import sk.itsovy.projectKlaufland.main.Globals;
import sk.itsovy.projectKlaufland.main.Internet;

public class Bill {
    private List<Item> list;
    private int count;
    private  double sum;
    private boolean open;
    private  Date date;

    public Bill() {
        this.list = new ArrayList<>();
        count = 0;
        open=true;
    }

    public List<Item> getList() {
        return list;
    }

    public Date getDate() {
        return date;
    }

    public void end() throws SQLException {
        if(open){
            date = new Date();
            System.out.println( date);
            Database db = Database.getInstanceDB();
            db.insertNewBill(this);

        }
        else {
            System.out.println("neni uzavreny");
        }

        open=false;

    }

    public void addItem(Item item) throws BillException {
        if (item != null) {
            if (!open){
                String message = "Bill is closed. Is not allowed to add new items";
                throw new BillException(message);
            }
            if (count == Globals.MAXTERMS) {
                String message = "Bill is fulll, max is " + Globals.MAXTERMS + " items.";
                throw new BillException(message);
            }
            list.add(item);
            count++;

        }
    }

    public void removeItem(Item item) {
        if (list.contains(item)){
            list.remove(item);
            count--;
        }
    }

    public double getFinalPrice(){
        sum=0;
        for(Item item: list){
            sum+=item.getTotalPrice();
        }
//        System.out.println("Final price: ");
        return sum;
//        throw new UnsupportedOperationException("Method does not exists yet");
    }

    public int getCount(){
        return count;
    }

    public void print(){
        if (count==0){
            System.out.println("Nothing to print. bill is empty");
        } else{
            for (Item item:list) {
                if(item instanceof DraftInterface) {
                    System.out.println("Name: "+item.getName() + " Count " + ((DraftInterface) item).getVolume()+" Price: "+item.getPrice() + " TotalPrice: " + item.getTotalPrice());
                }
                else if(item instanceof Fruit){
                    System.out.println("Name: "+item.getName() + " Count " + ((Fruit) item).getWeight()+" Price: "+item.getPrice() + " TotalPrice: " + item.getTotalPrice());
                }
                else if(item instanceof Piece){
                    System.out.println("Name: "+item.getName() + " Count " + ((Piece) item).getAmount() +" Price: "+item.getPrice() + " TotalPrice: " + item.getTotalPrice());
                }
            }
        }
    }

    public double getTotalPriceUSD() throws IOException {
        double totalPrice = getFinalPrice();
        double sum = totalPrice * Internet.getUSDrate();
        return sum;
    }

}
