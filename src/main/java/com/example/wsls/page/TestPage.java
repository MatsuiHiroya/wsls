package com.example.wsls.page;

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.wicketstuff.annotation.mount.MountPath;

@WicketHomePage
@MountPath("Home")
public class TestPage extends WebPage{
    public TestPage(){
        Integer integer = 0x1fa;
        var toRoomPageLink = new Link<>("toRoomPage"){
            @Override
            public void onClick(){
                setResponsePage(new RoomPage(1));
            }
        };
        add(toRoomPageLink);
    }
}