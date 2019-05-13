# demo1
popwindow,anko,模糊查询，自定义view，（刮刮卡）多图层，path

## 功能点
### 1.刮刮卡
1）实现步骤：
  a.draw里层中奖信息，或者里层图片
  b.draw外层图片 ，并在外层图片上画手指滑动的区域。核心方法自定义canvas，设置画笔
  ```
        //初始化
        outBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mCanvas=new Canvas(outBitmap);
        
        //ondraw(Canvas canvas)
        outerPaint.setXfermode(new PorterDuffXfermode((PorterDuff.Mode.DST_OUT)));
        mCanvas.drawPath(mPath, outerPaint);
  ```
 2）后期待补充逻辑
 
### 2.牛津词典
1）功能：附带查询提示功能。

2）待补充：
  a.更换数据库，比较sqlite，geendao和其他数据库的存储速度。
  b.数据源格式不正确，个别英文，如：“am” “an” 格式不正确，导致未存储到数据库中。
  c.权限问题。需要增加过渡页面

##爬坑

### 1.约束布局 ConstraintLayout
1)约束布局不能作为RecyclerView的父布局
2)约束布局作为副布局的时候，注意scrollview 的情况，可能会存在bug

总结：约束布局安全使用场景：子类都不为viewgroup，没有嵌套view

### 2.不要乱怀疑java源码的问题，多审核自己的代码逻辑


 
### 3.权限
 application，service中不能直接申请权限，只能在activity中申请
