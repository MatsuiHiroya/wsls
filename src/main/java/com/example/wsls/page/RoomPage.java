package com.example.wsls.page;

import com.example.wsls.bean.PostedRandom;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import java.util.Random;

@MountPath("room")
public class RoomPage extends WebPage {

    private Model randomModel;
    private Label participantRandomLabel;

    @SpringBean
    private IRoomPageService roomPageService;

    public RoomPage(Integer roomId){
        var flag = roomPageService.checkUserRole(roomId,1/*todo sessionから取得*/);

        var room = roomPageService.findRoom(roomId);
        //roomの名前、概要、期限をモデル化した後ラベルとして貼り付け
        var roomNameLabel = new Label("roomName",Model.of(room.getName()));
        var roomOverviewLabel = new Label("roomOverview",Model.of(room.getOverview()));
        var roomLimitTimeLabel = new Label("roomLimitTime",Model.of(room.getLimitTime()));
        add(roomNameLabel,roomOverviewLabel,roomLimitTimeLabel);

        //var sessionUserNameLabel = new Label("sessionUserName",Model.of("a"));
        var random = new Random();
        //var r = Byte.parseByte(hashForLottery.randomToHash(random.nextInt()));



        var LVWMC = new WebMarkupContainer("LVWMC");
        LVWMC.setOutputMarkupId(true);
        add(LVWMC);


        // Service からデータベースのユーザ一覧をもらい、Modelにする
        // List型のモデルは Model.ofList(...) で作成する。
        var postedRandomLM = Model.ofList(roomPageService.selectPostedRandom(roomId));

        // List型のモデルを表示する ListView
        var postedRandomLV = new ListView<>("participantList", postedRandomLM) {
            @Override
            protected void populateItem(ListItem<PostedRandom> listItem) {
                // List型のモデルから、 <li>...</li> ひとつ分に分けられたモデルを取り出す
                var itemModel = listItem.getModel();
                var participate = itemModel.getObject(); // 元々のListの n 番目の要素

                // インスタンスに入れ込まれたデータベースの検索結果を、列（＝フィールド変数）ごとにとりだして表示する
                // add する先が listItem になることに注意。
                var stringHash = roomPageService.randomToHash(Long.valueOf(participate.getRandom()));
                var participantIdLabel = new Label("participantId", Model.of(participate.getUserId()));
                listItem.add(participantIdLabel);

                participantRandomLabel = new Label("participantRandom", Model.of(stringHash));
                participantRandomLabel.setOutputMarkupId(true);
                listItem.add(participantRandomLabel);

                //抽選数値を公開するラベル　締め切りまで非表示
                var calcResultLabel = new Label("calcResultLabel",Model.of(""));
                calcResultLabel.setVisible(false);
                listItem.add(calcResultLabel);
            }
        };
        LVWMC.add(postedRandomLV);
        postedRandomLV.setOutputMarkupId(true);






        var randomSubmitForm = new Form<>("randomSubmitForm");
        add(randomSubmitForm);

        //Ajaxで書き換える
        var randomTextField = new TextField<>("randomTextField",Model.of(random.nextLong()));
        randomTextField.setOutputMarkupId(true);
        randomSubmitForm.add(randomTextField);

        /*var randomLabel = new Label("random",Model.of(random.nextLong()));
        randomLabel.setOutputMarkupId(true);
        add(randomLabel);*/


        var randomSubmitButton = new AjaxButton("randomSubmitButton"){
            @Override
            public void onSubmit(AjaxRequestTarget target){
                //var newRandom = hashForLottery.randomToHash(random.nextInt());
                /*randomLabel.setDefaultModelObject(random.nextLong());
                target.add(randomLabel);*/
                randomTextField.setDefaultModelObject(random.nextLong());
                target.add(randomTextField);

            }
        };
        randomSubmitForm.add(randomSubmitButton);


        var submitRandomButton = new AjaxButton("submitRandomButton"){
            @Override
            public void onSubmit(AjaxRequestTarget target){
                var nowRandom =  randomTextField.getDefaultModel().getObject().toString();
                System.out.println(nowRandom);
                roomPageService.insertRandom(roomId,1,"P",nowRandom);//todo userId,role書き換え
                var stringHash = roomPageService.randomToHash(Long.valueOf(nowRandom));
                participantRandomLabel.setDefaultModelObject(stringHash);
                target.add(participantRandomLabel);
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
        var deadlineButton = new Button("deadlineButton"){
            @Override
            public void onSubmit(){
                roomPageService.calculation(roomId);
            }
        };
        deadlineForm.add(deadlineButton);





        //乱数を元にハッシュを作成し、ラベルとして貼り付け
        //var randomLabel = new Label("random",Model.of(hashForLottery.randomToHash(random.nextInt())));
        //add(randomLabel);
        /*BigInteger tt = new BigInteger(t,16);
        System.out.println(tt);*/

    }

}
