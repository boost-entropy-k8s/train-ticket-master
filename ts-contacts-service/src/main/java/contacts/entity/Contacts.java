package contacts.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import contacts.tars.contacts.ContactsTars;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;

@Document(collection = "contacts")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contacts {

    @Id
    private UUID id;

    private UUID accountId;

    private String name;

    private int documentType;

    private String documentNumber;

    private String phoneNumber;

    public Contacts() {
        //Default Constructor
    }

    public Contacts(ContactsTars contactsTars) {
        this.id = UUID.fromString(contactsTars.getId());
        this.accountId=UUID.fromString(contactsTars.accountId);
        this.name=contactsTars.getName();
        this.documentType=contactsTars.getDocumentType();
        this.documentNumber=contactsTars.getDocumentNumber();
        this.phoneNumber=contactsTars.getPhoneNumber();
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDocumentType() {
        return documentType;
    }

    public void setDocumentType(int documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Contacts other = (Contacts) obj;
        return name.equals(other.getName())
                && accountId .equals( other.getAccountId() )
                && documentNumber.equals(other.getDocumentNumber())
                && phoneNumber.equals(other.getPhoneNumber())
                && documentType == other.getDocumentType();
    }

    public ContactsTars toTars(){
        ContactsTars contactsTars =new ContactsTars();
        contactsTars.setAccountId(this.accountId.toString());
        contactsTars.setDocumentNumber(this.documentNumber);
        contactsTars.setDocumentType(this.documentType);
        contactsTars.setId(this.id.toString());
        contactsTars.setName(this.name);
        contactsTars.setPhoneNumber(this.phoneNumber);
        return contactsTars;
    }
}
