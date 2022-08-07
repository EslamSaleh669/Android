package intalio.cts.mobile.android.viewer.viewer_menu;

import android.graphics.drawable.Drawable;


import java.util.Vector;

import intalio.cts.mobile.android.viewer.support.PDFConfig;

/**
 * Created by aem on 9/15/2017.
 */

public class MenuItem {

    private Drawable _menuImage;
    private Drawable _image;
    private String _menuText;
    private Vector<MenuItem> _subItems;
    private Vector<MenuItem> _backItems;
    private PDFConfig.AnnotationType _type;
    private boolean _isSelected;
    private boolean _isDisabled;
    private boolean _allPage;

    public String imageName;

    public MenuItem(Drawable _menuImage,String _menuText) {
        this._menuImage = _menuImage;
        this._menuText = _menuText;
        this._isSelected = false;
        this._isDisabled = false;
        this._allPage = false;
    }

    public MenuItem(Drawable _menuImage, PDFConfig.AnnotationType type,String _menuText) {
        this._menuImage = _menuImage;
        this._menuText = _menuText;
        this._type = type;
        this._isSelected = false;
        if(this._type == PDFConfig.AnnotationType.Back){
            _backItems = new Vector<>();
        }
        this._isDisabled = false;
        this._allPage = false;
    }

    public Drawable getImage() {
        return _image;
    }

    public void setImage(Drawable _image) {
        this._image = _image;
    }

    public void setMenuImage(Drawable menuImage) { this._menuImage = menuImage; }

    public Drawable getMenuImage(){
        return this._menuImage;
    }

    public String getMenuText(){
        return  this._menuText;
    }

    public Vector<MenuItem> getMenuItems(){
        return this._subItems;
    }

    public void addItem(MenuItem menuItem){
        if(this._subItems == null){
            this._subItems = new Vector<>();
        }
        this._subItems.add(menuItem);
    }

    public PDFConfig.AnnotationType getType(){
        return this._type;
    }

    public void addItems(Vector<MenuItem> items){
        if(this._subItems == null){
            this._subItems = new Vector<>();
        }
        this._subItems.addAll(items);
    }

    public boolean isSelected(){
        return this._isSelected;
    }

    public void setSelected(boolean selected){
        this._isSelected = selected;
    }

    public void setBackItems(Vector<MenuItem> backItems){
        _backItems.addAll(backItems);
    }

    public Vector<MenuItem> getBackItems(){
        return _backItems;
    }

    public boolean isDisabled(){
        return this._isDisabled;
    }

    public void setDisabled(boolean disable){
        this._isDisabled = disable;
    }

    public void setAllPage(boolean allPage){
        this._allPage = allPage;
    }

    public boolean isAllPage(){
        return this._allPage;
    }

}
