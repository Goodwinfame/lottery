package com.goodwin.singleton;

import com.goodwin.model.Member;
import com.goodwin.model.Members;
import com.goodwin.service.Imethods;
import com.goodwin.service.impl.MemberMethod;
import com.goodwin.utils.XmlUtil;

import java.io.File;
import java.util.*;

/**
 * Created by Superwen on 2017/1/7.
 */
public class MemberSingleton {
    private String path = new File(this.getClass().getResource("/").getPath()).toString().replace("classes", "") + "members.xml";

    private static HashMap<Integer, Member> memberMap = new HashMap<>();
    private static int faceIndex = 0;

    private static String[] faces = new String[]{
            "dist/images/angry-2.svg",
            "dist/images/arrogant.svg",
            "dist/images/baby-1.svg",
            "dist/images/baby-2.svg",
            "dist/images/binqilin3.svg",
            "dist/images/binqilin4.svg",
            "dist/images/camera.svg",
            "dist/images/crying-1.svg",
            "dist/images/crying.svg",
            "dist/images/device.svg",
            "dist/images/feeding_bottle2.svg",
            "dist/images/feeding.svg",
            "dist/images/feichuan.svg",
            "dist/images/huojian.svg",
            "dist/images/laptop.svg",
            "dist/images/lightbulb_idea.svg",
            "dist/images/shengdanliwu.svg",
            "dist/images/shengdanwazi.svg",
            "dist/images/tablet.svg",
            "dist/images/binqilin2.svg",
            "dist/images/dangao2.svg",
            "dist/images/dead-3.svg",
            "dist/images/gaogao3.svg",
            "dist/images/happy-10.svg",
            "dist/images/kiss-3.svg",
            "dist/images/nerd-3.svg",
            "dist/images/goutou.png",
            "dist/images/kaiche.gif",
            "dist/images/huaixiao.gif",
            "dist/images/liwu2.svg",
            "dist/images/tangguo2.svg",
            "dist/images/boluo.svg",
            "dist/images/huluobo.svg",
            "dist/images/jitui.svg",
            "dist/images/juzi.svg",
            "dist/images/liwu.svg",
            "dist/images/pingguo.svg",
            "dist/images/shouji1.svg",
            "dist/images/shouji2.svg",
            "dist/images/tangguo1.svg",
            "dist/images/xiangji1.svg",
            "dist/images/xiangji2.svg",
            "dist/images/xiangji3.svg",
            "dist/images/xiangjiao.svg"
    };

    private static final MemberSingleton instance = new MemberSingleton();

    private MemberSingleton() {
        List<Member> memberList = ((Members) XmlUtil.xml2Bean(path, Members.class)).getMembers();



        for (Member member: memberList){
            member.setPhoto(getFaceIndex());
            memberMap.put(member.getId(), member);
        }
    }

    public static MemberSingleton getInstance() {
        return instance;
    }

    public Member memberOnline(Integer id) {
        Iterator<Map.Entry<Integer, Member>> iterator = memberMap.entrySet().iterator();
        Member member = null;
        while (iterator.hasNext()){
            Map.Entry<Integer, Member> memberObj = iterator.next();
            member = memberObj.getValue();
            if (memberObj.getKey().equals(id)){
                member.setOnline(true);
                break;
            }

        }
        return member;

    }

    public void memberOffline(Integer id) {
        Iterator<Map.Entry<Integer, Member>> iterator = memberMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, Member> memberObj = iterator.next();
            if (memberObj.getKey().equals(id)){
                memberObj.getValue().setOnline(false);
                memberObj.getValue().setActivity(0l);
                break;
            }

        }
    }

    public List<Member> getAllMember(){
        return new ArrayList<>(memberMap.values());
    }

    public List<Member> getOnlineMember(){
        List<Member> onlineMember = new ArrayList<>();
        Iterator<Map.Entry<Integer, Member>> iterator = memberMap.entrySet().iterator();
        while (iterator.hasNext()){
            Member member = iterator.next().getValue();
            if (member.getOnline()){
                onlineMember.add(member);
            }

        }
        return onlineMember;
    }

    public List<Member> getUnprizedMember(){
        List<Member> unprizedMember = new ArrayList<>();
        Iterator<Map.Entry<Integer, Member>> iterator = memberMap.entrySet().iterator();
        while (iterator.hasNext()){
            Member member = iterator.next().getValue();
            if (member.getPrize() == null){
                unprizedMember.add(member);
            }

        }
        return unprizedMember;
    }

    public List<Member> getPrizedMember(){
        List<Member> prizedMember = new ArrayList<>();
        Iterator<Map.Entry<Integer, Member>> iterator = memberMap.entrySet().iterator();
        while (iterator.hasNext()){
            Member member = iterator.next().getValue();
            if (member.getPrize() != null){
                prizedMember.add(member);
            }

        }
        return prizedMember;
    }

    public Member getMember(Integer id) {
        return memberMap.get(id);
    }
    public Member getMember(Long phone) {
        Iterator<Map.Entry<Integer, Member>> iterator = memberMap.entrySet().iterator();
        while (iterator.hasNext()){
            Member member = iterator.next().getValue();
            if (member.getPhone().equals(phone)){
                return member;
            }

        }
        return null;
    }

    public boolean isOnline(Integer id) {
        return memberMap.get(id).getOnline();
    }

    public void addMember(Member member) {
        memberMap.put(member.getId(), member);
        List<Member> memberList = getAllMember();
        Members members = new Members();
        members.setMembers(memberList);
        XmlUtil.object2Xml(members, path);
    }

    public void changeMember(Member member) {
        memberMap.replace(member.getId(),member);
        Members members = new Members();
        members.setMembers(getAllMember());
        XmlUtil.object2Xml(members, path);
    }
    public void deleteMember(Member member) {
        memberMap.remove(member.getId());
        Members members = new Members();
        members.setMembers(getAllMember());
        XmlUtil.object2Xml(members, path);
    }
    private String getFaceIndex(){
        Random rand = new Random();
        while (true){
            int tempIndex = rand.nextInt(faces.length);
            if(faceIndex == tempIndex){
                continue;
            } else {
                faceIndex = tempIndex;
                break;
            }

        }
        return faces[faceIndex];
    }

}
