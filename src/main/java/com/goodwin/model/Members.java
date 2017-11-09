package com.goodwin.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Superwen on 2017/1/23.
 */
@XmlRootElement(name="members")
@XmlAccessorType(XmlAccessType.FIELD)
public class Members {
    @XmlElement(name="member")
    private List<Member> members = new ArrayList<>();

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void addMember(Member member) { this.members.add(member);}
}
