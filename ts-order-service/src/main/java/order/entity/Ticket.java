package order.entity;

import order.tars.order.TicketTars;

public class Ticket {

    private int seatNo;

    private String startStation;

    private String destStation;

    public Ticket(){

    }

    public Ticket(int seatNo, String startStation, String destStation) {
        this.seatNo = seatNo;
        this.startStation = startStation;
        this.destStation = destStation;
    }

    public int getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getDestStation() {
        return destStation;
    }

    public void setDestStation(String destStation) {
        this.destStation = destStation;
    }

    public TicketTars toTars(){
        TicketTars ticketTars = new TicketTars();
        ticketTars.setSeatNo(this.seatNo);
        ticketTars.setStartStation(this.startStation);
        ticketTars.setDestStation(this.destStation);
        return ticketTars;
    }
}
