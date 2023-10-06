//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.openpaas.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect
public class ServiceInstanceLastOperation {
    @JsonSerialize
    private String description;
    private OperationState state;

    public ServiceInstanceLastOperation() {
    }

    public ServiceInstanceLastOperation(String description, OperationState operationState) {
        this.setDescription(description);
        this.state = operationState;
    }

    public String getDescription() {
        return this.description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    @JsonSerialize
    public String getState() {
        switch (this.state) {
            case IN_PROGRESS:
                return "in progress";
            case SUCCEEDED:
                return "succeeded";
            case FAILED:
                return "failed";
            default:
                assert false;

                return "internal error";
        }
    }

    @JsonSerialize
    public void setState(int state) {
        switch (state) {
            case 0:
                this.state = OperationState.IN_PROGRESS;
                break;
            case 1:
                this.state = OperationState.SUCCEEDED;
                break;
            case 2:
                this.state = OperationState.FAILED;
                break;
            default:
                assert false;
        }

    }
}
