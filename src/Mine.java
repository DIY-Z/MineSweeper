import javax.swing.*;

public class Mine extends JLabel {
    //Mine所在位置
    private int posX;
    private int posY;
    private boolean isBomb;       //是否为炸弹区域
    private int arroundBombNums;    //周围的炸弹数
    private boolean isFried;          //是否炸了，前提条件是它是炸弹

    public Mine(int x, int y){
        this.posX = x;
        this.posY = y;
        isBomb = false;         //默认不是炸弹
        arroundBombNums = 0;
        isFried = false;
    }

    public boolean isFried() {
        return isFried;
    }

    public void setFried(boolean fried) {
        isFried = fried;
    }

    public int getPosX() {
        return posX;
    }


    public void setPosX(int posX) {
        this.posX = posX;
    }

    //获得纵坐标
    public int getPosY() {
        return posY;
    }

    //设置纵坐标
    public void setPosY(int posY) {
        this.posY = posY;
    }

    //判断是否为炸弹
    public boolean isBomb() {
        return isBomb;
    }

    //设置是否为炸弹
    public void setBomb(boolean bomb) {
        isBomb = bomb;
    }

    //获得周围的炸弹的数目
    public int getArroundBombNums() {
        return arroundBombNums;
    }

    //修改周围的炸弹的数目
    public void setArroundBombNums(int arroundBombNums) {
        this.arroundBombNums = arroundBombNums;
    }
}
