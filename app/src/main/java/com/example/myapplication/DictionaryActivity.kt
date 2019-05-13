package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select

class DictionaryActivity() : Activity(),AnkoLogger{
    private lateinit var tv_reuslt: TextView;
    private lateinit var et_search: EditText;
    private lateinit var btn_search: Button;
    private lateinit var popupWindow: PopupWindow
    private lateinit var recyclerView: RecyclerView

    val log = AnkoLogger(this.javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)
        tv_reuslt=find<TextView>(R.id.tv_reuslt);
        et_search=find<EditText>(R.id.et_search);
        btn_search=find<Button>(R.id.btn_search);
        et_search.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                var name=et_search.text.toString();
                if(name.length<1){
                    if(popupWindow!=null)
                        popupWindow.dismiss()
                    return
                }
                if(TextUtils.isEmpty(et_search.text.toString())){
                    toast("输入不能为null")
                }else{
                    if(App.me.isDone) {
                        database.use {
                        var list = select("table_dic","name","content")
                                    .whereArgs("name like {name}", "name" to name+"%")
                                    .limit(3)
                                    .exec { parseList(classParser<Dic>()) }
                            showPopWindow(list);
                        }
                    }else{
                        toast("字典加载中("+App.me.getReadRate()+")，请稍后")
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        btn_search.setOnClickListener {
            if(App.me.isDone){
                var text=et_search.text.toString()
                if(TextUtils.isEmpty(text)) {
                    toast("请输入单词")
                }else{
                    if(popupWindow!=null)
                        popupWindow.dismiss();
                database.use {
                    var list=select("table_dic","name","content")
                            .whereSimple("name=?" , text)
                            .exec { parseList(classParser<Dic>()) }
                    if(list.isEmpty()){
                        toast("查无此单词")
                    }else
                        tv_reuslt.setText(list.get(0).name+"\n"+list.get(0).content)
                }
                }
            }else{
                toast("字典加载中("+App.me.getReadRate()+")，请稍后")
            }
        }
        initPopWindow();

    }

    private lateinit var adapter: DicSearchAdapter

    private val lists: ArrayList<Dic> = ArrayList()

    private fun initPopWindow() {
        //如果RecyclerView父布局是ConstraintLayout，会不显示；ConstraintLayout在有scrollview的时候也要注意
        //pop布局最好用WRAP_CONTENT否则显示不全
//        var view=layoutInflater.inflate(R.layout.popwindow_dic,null);
//        recyclerView = view.findViewById<RecyclerView>(R.id.rv)
        recyclerView= RecyclerView(this)
        recyclerView.padding=10
        recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerView.setBackgroundResource(R.drawable.green)
        adapter= DicSearchAdapter(lists,object:OnCallBack{
            override fun onResult(name: String) {
                et_search.setText(name)
                popupWindow.dismiss()
            }
        },this)
        recyclerView.adapter=adapter
        popupWindow= PopupWindow(recyclerView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private fun showPopWindow(list: List<Dic>) {
        popupWindow.showAsDropDown(et_search,0,0)

        if(list.size<1) {
            popupWindow.dismiss()
            return
        }
        for (i in 0..list.size-1){
            log.info(list[i].name)
        }
        lists.clear()
        lists.addAll(list)
        adapter.notifyDataSetChanged()

    }




}

