package com.hammer.lei.domain.response;

import com.hammer.lei.domain.response.component.WechatHttpResponseBase;
import com.hammer.lei.domain.shared.Contact;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchGetContactResponse extends WechatHttpResponseBase {
    @JsonProperty
    private Set<Contact> ContactList;
    @JsonProperty
    private int Count;

    public Set<Contact> getContactList() {
        return ContactList;
    }

    public void setContactList(Set<Contact> contactList) {
        ContactList = contactList;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }
}
