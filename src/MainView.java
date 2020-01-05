import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.util.zip.DeflaterInputStream;

public class MainView extends JFrame implements ActionListener {

    public static int rows = 10;             //地雷地图对应的行的个数
    public static int columns = 10;          //地雷地图对应的列的个数
    public static int numOfBombs = 20;       //地雷的数目
    public static int sumOfSafe = rows * columns - numOfBombs;            //安全区的个数
    public int[][] visits;
    private static boolean isWin = false;
    private MouseListener listener;     //鼠标监听器

    /*----菜单栏对应的一些组件----*/
    private JMenu game;
    private JMenuItem exit;
    private JMenuItem first_level;
    private JMenuItem second_level;
    private JMenuItem third_level;
    private JMenuBar menuBar;

    JPanel mine_map;            //用于放入雷区与非雷区画面
    /*----区域地图----*/
    private Mine[][] mines = null;

    public MainView(){
        setTitle("扫雷");
        visits = new int[600][600];
        //添加菜单
        AddMenu();
        //加载游戏画面
        initGameView();
        //配置鼠标监听器
        configurateListener();
        //设置一些区域为地雷
        setBombs();
        //确定每个区域周围的雷数
        confirmBombsNumOfArea();

        this.setVisible(true);
        this.setSize(190,220);
        this.setResizable(false);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //添加菜单
    private void AddMenu() {
        /*----设计菜单----*/
        game = new JMenu("菜单");
        exit = new JMenuItem("结束游戏");
        first_level = new JMenuItem("容易");
        second_level = new JMenuItem("中等");
        third_level = new JMenuItem("困难");
        //为菜单添加菜单项
        game.add(exit);
        game.add(first_level);
        game.add(second_level);
        game.add(third_level);
        //设置事件监听器
        exit.addActionListener(this);
        first_level.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rows = 10;
                columns = 10;
                numOfBombs = 20;
                sumOfSafe = rows * columns - numOfBombs;
                isWin = false;
                restartGame();
            }
        });
        second_level.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rows = 15;
                columns = 20;
                numOfBombs = 50;
                sumOfSafe = rows * columns - numOfBombs;
                isWin = false;
                restartGame();
            }
        });
        third_level.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rows = 25;
                columns = 30;
                numOfBombs = 200;
                sumOfSafe = rows * columns - numOfBombs;
                isWin = false;
                restartGame();
            }
        });
        //将菜单添加到菜单栏中
        menuBar = new JMenuBar();
        menuBar.add(game);
        this.setJMenuBar(menuBar);
    }

    //确定每个区域周围雷的个数
    private void confirmBombsNumOfArea() {
        int dx[] = {-1, -1, -1, 0, 0, 1, 1, 1};
        int dy[] = {0, 1, -1, 1, -1, 0, 1, -1};
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(mines[i][j].isBomb()){
                    mines[i][j].setArroundBombNums(0);
                    continue;
                }
                int count = 0;      //周围的炸弹的个数
                for(int k = 0; k < 8; k++){
                    int x = mines[i][j].getPosX() + dx[k];
                    int y = mines[i][j].getPosY() + dy[k];
                    if(x >= 0 && x < rows && y >= 0 && y < rows && mines[x][y].isBomb()){
                        ++count;
                    }
                }
                mines[i][j].setArroundBombNums(count);
            }
        }
    }

    //设置哪些区域为地雷
    private void setBombs() {
        int count = 0;      //表示已经设置完成了多少个雷区
        Random random = new Random();
        while(count < numOfBombs){
            int x = random.nextInt(rows);
            int y = random.nextInt(columns);
            if(!mines[x][y].isBomb()){
                //若该区域不是雷区则设置成雷区
                mines[x][y].setBomb(true);
                ++count;
            }
        }
    }

    //配置鼠标监听器
    public void configurateListener(){
        listener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) {
                //获得对应的区域
                if(e == null || e.getSource() instanceof Mine == false){
                    return ;
                }
                Mine a = (Mine)(e.getSource());
                if(a.isBomb()){
                    a.setIcon(new ImageIcon(this.getClass().getResource("flood_bomb.gif")));
                    a.setFried(true);
                    //显示所有炸弹位置
                    showBombLocation();
                    JOptionPane.showMessageDialog(null,"你失败了");
                    //重新开始
                    isWin = false;
                    rows = 10;
                    columns = 10;
                    numOfBombs = 20;
                    sumOfSafe = rows * columns - numOfBombs;
                    restartGame();
                }else{
                    a.setIcon(new ImageIcon( this.getClass().getResource(a.getArroundBombNums()+".gif")));
                    if(a.getArroundBombNums() != 0){
                        return ;
                    }else if(a.getArroundBombNums() == 0){
                        dfs(a.getPosX(), a.getPosY());
                    }
                    if(isWin == true){
                        JOptionPane.showMessageDialog(null,"你成功了");
                        //重新开始
                        isWin = false;
                        rows = 10;
                        columns = 10;
                        numOfBombs = 20;
                        sumOfSafe = rows * columns - numOfBombs;
                        restartGame();
                    }

                }
            }

            //显示所有炸弹的位置
            private void showBombLocation() {
                for(int i = 0; i < rows; i++){
                    for(int j = 0; j < columns; j++){
                        if(mines[i][j].isBomb() && !mines[i][j].isFried()){
                            mines[i][j].setIcon(new ImageIcon(this.getClass().getResource("bomb.gif")));
                        }else if(mines[i][j].isBomb() && mines[i][j].isFried()){
                            continue;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        };
    }


    //采用深度优先搜索遍历
    public void dfs(int x, int y){
        if(sumOfSafe <= 0){
            isWin = true;
            return ;
        }
        if(x >= 0 && x < rows && y >= 0 && y < columns && visits[x][y] == 0 && !mines[x][y].isBomb() && mines[x][y].getArroundBombNums() == 0){
            --sumOfSafe;
            visits[x][y] = 1;
            mines[x][y].setIcon(new ImageIcon(this.getClass().getResource("0.gif")));
            dfs(x - 1, y);
            dfs(x - 1, y + 1);
            dfs(x - 1, y - 1);
            dfs(x, y + 1);
            dfs(x, y - 1);
            dfs(x + 1, y);
            dfs(x + 1, y + 1);
            dfs(x + 1, y - 1);
        }else if(x >= 0 && x < rows && y >= 0 && y < columns && visits[x][y] == 0 && !mines[x][y].isBomb() && mines[x][y].getArroundBombNums() != 0){
            --sumOfSafe;
            visits[x][y] = 1;
            mines[x][y].setIcon(new ImageIcon(this.getClass().getResource(mines[x][y].getArroundBombNums() + ".gif") ));
            return ;
        }
        return ;
    }


    //加载游戏画面
    public void initGameView(){
        mines = new Mine[rows][columns];
        mine_map = new JPanel();
        //将该面板设置为网状布局，rows行，columns列
        mine_map.setLayout(new GridLayout(rows, columns));

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                mines[i][j] = new Mine(i, j);
                mines[i][j].setIcon(new ImageIcon(this.getClass().getResource("blind_area.gif")));
                mines[i][j].addMouseListener(listener);
                mine_map.add(mines[i][j]);
            }
        }
        //设置背景颜色为灰色
        mine_map.setBackground(Color.lightGray);
        //添加该面板
        this.add(mine_map);
        //自适应大小
        this.pack();
    }

    //重新开始游戏
    public void restartGame(){
        visits = new int[600][600];
        this.remove(mine_map);

        //重新加载游戏画面
        initGameView();
        //配置鼠标监听器
        configurateListener();
        //设置一些区域为地雷
        setBombs();
        //确定每个区域周围的雷数
        confirmBombsNumOfArea();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == exit){
            JOptionPane.showMessageDialog(this, "你确定要退出吗？");
            System.exit(0);
        }
    }

    public static void main(String[] args){
        new MainView();
    }
}
