package other.entity;

import other.tars.orderother.TicketTars;

public class Ticket {

    private int seatNo;

    private String startStation;

    private String destStation;

    public Ticket(){

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
