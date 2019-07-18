package config.entity;


import config.tars.config.ConfigTars;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Document(collection="config")
public class Config {
    @Valid
    @Id
    @NotNull
    private String name;

    @Valid
    @NotNull
    private String value;

    @Valid
    private String description;

    public Config() {
    }

    public Config(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConfigTars toTars(){
        ConfigTars configTars = new ConfigTars();
        configTars.setDescription(this.description);
        configTars.setName(this.name);
        configTars.setValue(this.value);
        return configTars;
    }
}
