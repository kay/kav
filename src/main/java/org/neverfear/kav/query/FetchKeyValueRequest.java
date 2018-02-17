package org.neverfear.kav.query;

public class FetchKeyValueRequest {
    private final String key;

    public FetchKeyValueRequest(String key) {
        super();
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FetchKeyValueRequest other = (FetchKeyValueRequest) obj;
        if (this.key == null) {
            if (other.key != null)
                return false;
        } else if (!this.key.equals(other.key))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FetchKeyValue [key=").append(this.key).append("]");
        return builder.toString();
    }

}
