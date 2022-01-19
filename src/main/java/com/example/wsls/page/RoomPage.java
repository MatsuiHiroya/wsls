package com.example.wsls.page;

import com.example.wsls.bean.PostedRandom;
import com.example.wsls.bean.Room;
import com.example.wsls.service.IRoomPageService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@MountPath("room")
public class RoomPage extends WebPage {

    private Room room;
    private Model randomModel;
    private Label participantRandomLabel;
    private Label calcResultLabel;
    private IModel<List<PostedRandom>> postedRandomLM;
    private ListView postedRandomLV;
    //抽選が開催中か否かを判断するフラグ
    private boolean roomFlag;
    private Label sponsorNameLabel;
    private Label sponsorRandomLabel;
    private List<PostedRandom> winners;

    @SpringBean
    private IRoomPageService roomPageService;

    public RoomPage(Integer roomId){
        room = roomPageService.findRoom(roomId);
        //ユーザが参加者か主催者なのか判断するフラグ
        var roleFlag = roomPageService.checkUserRole(roomId,1/*todo sessionから取得*/);
        //todo　締め切りに応じてフラグ変更
        roomFlag = room.isFlag();

        //roomの名前、概要、期限をモデル化した後ラベルとして貼り付け
        var roomNameLabel = new Label("roomName",Model.of(room.getName()));
        var roomOverviewLabel = new Label("roomOverview",Model.of(room.getOverview()));
        var roomLimitTimeLabel = new Label("roomLimitTime",Model.of(room.getLimitTime()));
        var numberOfPW = new Label("numberOfPW",Model.of(room.getWinners()));
        add(roomNameLabel,roomOverviewLabel,roomLimitTimeLabel,numberOfPW);


        //当選者の情報用のWMC
        var winnersWMC = new WebMarkupContainer("winnersWMC");
        winnersWMC.setOutputMarkupId(true);
        add(winnersWMC);
        winnersWMC.setVisible(false);



        //主催者の名前と乱数　締め切りまで未公開
        String sponsorName,sponsorRandom;
        if(!roomFlag) {
            sponsorName = room.getAuthorId().toString();
            var sponsorInfo = roomPageService.findSponsorInfo(roomId,room.getAuthorId());
            sponsorRandom = sponsorInfo.getRandom().toString();
        }else{
            sponsorName = "unpublished";
            sponsorRandom = "unpublished";
        }
        sponsorNameLabel = new Label("sponsorName",Model.of(sponsorName));
        sponsorRandomLabel = new Label("randomOfSponsor", Model.of(sponsorRandom));
        sponsorNameLabel.setOutputMarkupId(true);
        sponsorRandomLabel.setOutputMarkupId(true);
        add(sponsorNameLabel,sponsorRandomLabel);


        var random = new Random();
        //参加者のリストビューを動的に変化させるために用いるWebMarkupContainer
        var LVWMC = new WebMarkupContainer("LVWMC");
        LVWMC.setOutputMarkupId(true);
        add(LVWMC);

        // Service からデータベースのユーザ一覧をもらい、Modelにする
        // List型のモデルは Model.ofList(...) で作成する。
        //roomIdで検索し、参加者と主催者の情報をListとして持ってくる
        postedRandomLM = Model.ofList(roomPageService.selectPostedRandom(roomId));
        //持ってきたListを元に、ListViewを用いてList型のモデルを表示する
        postedRandomLV = new ListView<>("participantList", postedRandomLM) {
            @Override
            protected void populateItem(ListItem<PostedRandom> listItem) {
                // List型のモデルから、 <li>...</li> ひとつ分に分けられたモデルを取り出す
                var itemModel = listItem.getModel();
                var participate = itemModel.getObject(); // 元々のListの n 番目の要素

                // インスタンスに入れ込まれたデータベースの検索結果を、列（＝フィールド変数）ごとにとりだして表示する
                // add する先が listItem になることに注意。
                var participantIdLabel = new Label("participantId", Model.of(participate.getUserId()));
                listItem.add(participantIdLabel);

                //trueなら、まだ締め切り前なのでハッシュ化して公開 / falseなら、締め切り後なのでそのまま公開
                String strRandom;
                if(roomFlag){
                    //strRandom = roomPageService.randomToHash(participate.getRandom());
                    strRandom = "unpublished";
                }else{
                    strRandom = participate.getRandom().toString();
                }
                participantRandomLabel = new Label("participantRandom", Model.of(strRandom));
                //このラベルは、ボタンを押すことによって動的に変化させたいのでsetOutputMarkupIdをtrueにする
                participantRandomLabel.setOutputMarkupId(true);
                listItem.add(participantRandomLabel);

                //抽選数値を公開するラベル
                calcResultLabel = new Label("calcResultLabel",Model.of(participate.getResult()));
                calcResultLabel.setOutputMarkupId(true);
                listItem.add(calcResultLabel);
            }
        };
        LVWMC.add(postedRandomLV);
        postedRandomLV.setOutputMarkupId(true);


        var userInfoWMC = new WebMarkupContainer("userInfo");
        add(userInfoWMC);

        //乱数を提出するフォーム
        var randomSubmitForm = new Form<>("randomSubmitForm");
        userInfoWMC.add(randomSubmitForm);
        //乱数提出用のTextField　Ajaxで動的に書き換える
        var randomTextField = new TextField<>("randomTextField",Model.of(random.nextLong()));
        randomTextField.setOutputMarkupId(true);
        randomSubmitForm.add(randomTextField);

        //乱数提出用のボタン　押下時に乱数をTextFieldに自動入力する
        var changeRandomButton = new AjaxButton("changeRandomButton"){
            @Override
            public void onSubmit(AjaxRequestTarget target){
                randomTextField.setDefaultModelObject(random.nextLong());
                target.add(randomTextField);
            }
        };
        randomSubmitForm.add(changeRandomButton);

        //乱数提出用のボタン
        var submitRandomButton = new AjaxButton("submitRandomButton"){
            @Override
            public void onSubmit(AjaxRequestTarget target){
                //TextField内に記入された乱数を持ってくる
                var nowRandom =  randomTextField.getDefaultModel().getObject().toString();
                System.out.println("提出された乱数" + nowRandom);
                //乱数をDBに登録
                roomPageService.insertRandom(roomId,1,"P",nowRandom);//todo userId,role書き換え
                //Listview内の情報を更新
                postedRandomLM.setObject(roomPageService.selectPostedRandom(roomId));
                /*var stringHash = roomPageService.randomToHash(Long.valueOf(nowRandom));
                participantRandomLabel.setDefaultModelObject(stringHash);*/

                target.add(/*participantRandomLabel,*/LVWMC);
            }
        };
        randomSubmitForm.add(submitRandomButton);


        /*
        var submitRandomButton = new Button("submitRandomButton"){
            @Override
            public void onSubmit(){
                var nowRandom =  randomTextField.getDefaultModel().getObject().toString();
                System.out.println(nowRandom);
                roomPageService.insertRandom(roomId,1,"P",nowRandom);//todo userId,role書き換え
            }
        };
        randomSubmitForm.add(submitRandomButton);*/


        /*
        //乱数を提出するためのフォームの作成
        var submitForm = new Form<>("submitForm");
        add(submitForm);
        //乱数を提出するためのボタンの作成
        var submitButton = new Button("submitButton"){
            @Override
            public void onSubmit(){
                var nowRandom =  randomTextField.getDefaultModel().getObject().toString();
                System.out.println(nowRandom);
                roomPageService.insertRandom(roomId,1,"P",nowRandom/*todo userId,role書き換え*///);
        //    }
        //};
        //submitForm.add(submitButton);*/

        //主催者用の抽選の締め切りボタンとそのフォームを作成
        var deadlineForm = new Form<>("deadlineForm");
        add(deadlineForm);
        //締め切りボタン　ボタンを押すと結果を表示して、1度押すと押せないようにする
        var deadlineButton = new AjaxButton("deadlineButton"){
            @Override
            public void onSubmit(AjaxRequestTarget target){
                roomPageService.closeLottery(roomId);
                //ListViewの中身書き換え　結果表示
                //changeLVModel(roomId);
                var lotteryResultList = roomPageService.calculation(roomId);
                postedRandomLM.setObject(lotteryResultList);
                //主催者の情報も公開
                var sponsorInfo = roomPageService.findSponsorInfo(roomId,room.getAuthorId());
                sponsorNameLabel.setDefaultModelObject(room.getAuthorId().toString());
                sponsorRandomLabel.setDefaultModelObject(sponsorInfo.getRandom().toString());
                //締め切りボタンと乱数提出ボタンを押せなくする
                this.setVisible(false);
                submitRandomButton.setVisible(false);
                userInfoWMC.setVisible(false);
                winnersWMC.setVisible(true);
                target.add(this,LVWMC,sponsorNameLabel,sponsorRandomLabel,userInfoWMC);
            }
        };
        deadlineButton.setOutputMarkupId(true);
        deadlineForm.add(deadlineButton);





        //falseなら締め切り　最初から結果表示
        if (!roomFlag) {
            changeLVModel(roomId);
            //changeSponsorInfo(roomId, room.getAuthorId());
            //var sponsorInfo = roomPageService.findSponsorInfo(roomId,room.getAuthorId());
            //sponsorRandomLabel = new Label("randomOfSponsor",Model.of(sponsorInfo.getRandom()));
            //締め切りボタンと乱数提出ボタンを押せなくする
            deadlineButton.setVisible(false);
            submitRandomButton.setVisible(false);
            userInfoWMC.setVisible(false);
            winnersWMC.setVisible(true);
        }

        var winnersLM = Model.ofList(winners);
        var winnersLV = new ListView<>("winnersList",winnersLM){
            @Override
            protected void populateItem(ListItem<PostedRandom> listItem) {
                var itemModel = listItem.getModel();
                var winners = itemModel.getObject();

                var winnersIdLabel = new Label("winnersId", Model.of(winners.getUserId()));
                listItem.add(winnersIdLabel);
                var winnersRandomLabel = new Label("winnersRandom",Model.of(winners.getRandom()));
                listItem.add(winnersRandomLabel);
                var winnersResultLabel = new Label("winnersResult",Model.of(winners.getResult()));
                listItem.add(winnersResultLabel);
            }
        };
        winnersWMC.add(winnersLV);


    }

    public void changeLVModel(Integer roomId){
        //抽選数値などが記述されたリストを受け取り、リストビューで表示する内容を変更
        var lotteryResultList = roomPageService.calculation(roomId);
        postedRandomLM.setObject(lotteryResultList);
        winners = new ArrayList<>();
        for (int i=0;i<room.getWinners();i++){
            winners.add(lotteryResultList.get(i));
        }
    }

    public void changeSponsorInfo(Integer roomId,Integer sponsorId){
        var sponsorInfo = roomPageService.findSponsorInfo(roomId,sponsorId);
       // sponsorNameLabel.setDefaultModelObject(Model.of(sponsorId.toString()));
       // sponsorRandomLabel.setDefaultModelObject(Model.of(sponsorInfo.getRandom().toString()));
        sponsorRandomLabel = new Label("randomOfSponsor",Model.of(sponsorInfo.getRandom()));
    }

}
