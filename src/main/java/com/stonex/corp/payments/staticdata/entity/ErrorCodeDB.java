package com.stonex.corp.payments.staticdata.entity;

import com.stonex.corp.payments.staticdata.error.AppError;
import com.stonex.corp.payments.staticdata.error.ErrorItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "errorcodes")
public class ErrorCodeDB {
    @Id
    private String _id;
    private String language;
    private String applicationid;
    private String errorcode;
    private String description;

    public ErrorCodeDB(String errorcode, String errordescription){
        this.language="en";
        this.applicationid="STATICDATA";
        this.errorcode = errorcode;
        this.description = errordescription;
    }

    public ErrorItem getErrorItem(String target){
        return new ErrorItem(this.errorcode,target, this.description);
    }

    public void parseKeywords(){
        if (this.description!=null){
            if (this.description.equalsIgnoreCase("")){
                this.description = "Empty Error Description for Error Code "+this.errorcode;
            }
        } else {
            this.description = "Missing Error Code "+this.errorcode;
        }
    }

    public void parseKeywords(HashMap<String, String> keywordmap){
        if (this.description!=null){
            if (this.description.equalsIgnoreCase("")){
                this.description = "Empty Error Description for Error Code "+this.errorcode;
            }
            if (keywordmap!=null){
                for (Map.Entry<String, String> entry : keywordmap.entrySet()){
                    if (this.description.indexOf(entry.getKey().toUpperCase().trim())!=-1? true:false){
                        this.description = this.description.replace(entry.getKey(),entry.getValue());
                    }
                }
            }
        } else {
            this.description = "Missing Error Code "+this.errorcode;
        }
    }

}
