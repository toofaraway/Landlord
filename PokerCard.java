package client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.Hand;

public class PokerCard {
	private static PokerCard pokerCard;
	Map<Integer,IconSet> pics;
	Map<JButton, Integer> buttons;
	//public Integer card;
	
	private PokerCard() {
		String path = "poker/";		//图片所在目录
		pics = new HashMap<Integer, IconSet>();
		buttons = new HashMap<JButton, Integer>();
		List<Integer> poker = new ArrayList<Integer>();
		poker.add(0);
		poker.add(-1);
		poker.add(-2);
		for(Integer i = 2; i < 54; i++) poker.add(i);
		for(Integer i : poker) {
			IconSet iconSet = new IconSet();
			iconSet.icon = new ImageIcon(PokerCard.class.getResource(path + i.toString() + ".jpg"));
			BufferedImage bi = toBufferedImage(iconSet.icon.getImage());
			iconSet.partIcon = new ImageIcon(bi.getSubimage(0, 0, iconSet.icon.getIconWidth()/6, iconSet.icon.getIconHeight()));
			//东向ICON
			BufferedImage biEast = rotateCounterclockwise90(bi);
			iconSet.iconE = new ImageIcon(biEast);
			iconSet.partIconE = new ImageIcon(biEast.getSubimage(0, iconSet.iconE.getIconHeight()/6*5, iconSet.iconE.getIconWidth(), iconSet.iconE.getIconHeight()/6));
			//北向ICON
			BufferedImage biNorth = rotate180(bi);
			iconSet.iconN = new ImageIcon(biNorth);
			iconSet.partIconN = new ImageIcon(biNorth.getSubimage(iconSet.iconN.getIconWidth()/6*5, 0, iconSet.iconN.getIconWidth()/6, iconSet.iconN.getIconHeight()));
			//西向ICON
			BufferedImage biWest = rotateClockwise90(bi);
			iconSet.iconW = new ImageIcon(biWest);
			iconSet.partIconW = new ImageIcon(biWest.getSubimage(0, 0, iconSet.iconW.getIconWidth(), iconSet.iconW.getIconHeight()/6));
			pics.put(i, iconSet);
		}
	}

	public static PokerCard getInstance() throws IOException {
		if(pokerCard == null) {
			pokerCard = new PokerCard();
		}
		return pokerCard;
	}
	
	public void printHand(Hand hand, List<String> players, String player, Menu menu) {
		/*
		 * 每1/6张牌占位计算（重叠5/6），两边的牌占窗口高度的2/5，按全部牌计算
		 * dim = 2/5 * height /25(38)
		 */
		//所有所有方向玩家锁对应的游标值
		int west = 0;
		int east = 0;
		int south = 0;
		int north = 0;
		int index = 0;
		for(String name : players) {
			if(name.equals(player)) break;
			index++;
		}
		south = index;	//玩家游标值
		if(index == players.size() -1) index = 0;
		else index++;
		east = index;	//东家游标值
		if(index == players.size() -1) index = 0;
		else index++;
		if(players.size() == 4) {
			north = index;	//北家游标值
			if(index == players.size() -1) index = 0;
			else index++;
		}
		west = index;	//西家游标值
		//确定hand所在的方位
		index = 0;
		for(String name : players) {
			if(name.equals(hand.player)) break;
			index++;
		}
		JPanel pan = new JPanel();
		pan.setLayout(new FlowLayout(1, 1, 1));//居中对齐，水平、垂直间距各1个像素
		int dim;
		dim = menu.client.getHeight() * 2/ 5 / (players.size() == 4 ? 38 : 25);
		int width = (dim)*(hand.hand.size()+9);
		int height = dim*7*3/2+1;	//加2个间隔像素
		pan.setPreferredSize(new Dimension(width, height));
		for(int i = 0; i < hand.hand.size() -1; i++) {
			ImageIcon icon0 = pics.get(hand.hand.get(i)).partIcon;
			ImageIcon icon = new ImageIcon();
			icon.setImage(icon0.getImage().getScaledInstance(dim, 6*dim/2*3, Image.SCALE_DEFAULT));
			JButton card = new JButton();
			card.setIcon(icon);
			card.setBorder(null);
			pan.add(card);
		}
		//最后一张显示全图
		ImageIcon lastIcon0 = pics.get(hand.hand.get(hand.hand.size() - 1)).icon;
		ImageIcon lastIcon = new ImageIcon();
		lastIcon.setImage(lastIcon0.getImage().getScaledInstance(6*dim, 6*dim/2*3, Image.SCALE_DEFAULT));
		JButton last = new JButton();
		last.setIcon(lastIcon);
		last.setBorder(null);
		pan.add(last);		
		pan.setVisible(true);
		if(index == south) {
			menu.handS.removeAll();
			menu.handS.add(pan);
		}else if(index == east) {
			menu.handE.removeAll();
			menu.handE.add(pan);
		}else if(index == north && players.size() == 4) {
			menu.handN.removeAll();
			menu.handN.add(pan);
		}else if(index == west) {
			menu.handW.removeAll();
			menu.handW.add(pan);
		}
		pan.updateUI();
	}
	
	public void printInHand(Hand hand, List<String> players, String player, Menu menu) {
		/*
		 * 每1/6张牌占位计算（重叠5/6），两边的牌占窗口高度的3/5，按全部牌计算
		 * dim = 3/5 * height /25(38)
		 */
		//所有所有方向玩家锁对应的游标值
		int west = 0;
		int east = 0;
		int north = 0;
		int index = 0;
		for(String name : players) {
			if(name.equals(player)) break;
			index++;	////玩家游标值
		}
		if(index == players.size() -1) index = 0;
		else index++;
		east = index;	//东家游标值
		if(index == players.size() -1) index = 0;
		else index++;
		if(players.size() == 4) {
			north = index;	//北家游标值
			if(index == players.size() -1) index = 0;
			else index++;
		}
		west = index;	//西家游标值
		//确定hand所在的方位
		index = 0;
		for(String str : players) {
			if(str.equals(hand.player)) break;
			index++;
		}
		JPanel pan = new JPanel();
		pan.setLayout(new FlowLayout(1, 1, 1));//居中对齐，水平、垂直间距各1个像素
		int dim = menu.client.getHeight() * 3/ 5 / (players.size() == 4 ? 37 : 24);
		if(hand.player.equals(player)) {
			buttons.clear();
			if(hand.hand.size() == 0) {
				((JPanel) menu.inHandS.getComponent(0)).removeAll();
				JLabel jl = new JLabel("出完啦！");
				jl.setPreferredSize(new Dimension(dim*6/2*3+2, dim*6/2*3+2));
				((JPanel) menu.inHandS.getComponent(0)).add(jl);				
				return;
			}
			for(int i = 0; i < hand.hand.size() -1; i++) {
				ImageIcon icon0 = pics.get(hand.hand.get(i)).partIcon;
				ImageIcon icon = new ImageIcon();
				icon.setImage(icon0.getImage().getScaledInstance(dim, 6*dim/2*3, Image.SCALE_DEFAULT));
				JButton cardS = new JButton();
				cardS.setIcon(icon);
				cardS.setBorder(null);
				cardS.addActionListener(menu.handL);
				pan.add(cardS);
				buttons.put(cardS, i);
			}			
			//最后一张显示全图
			ImageIcon lastIcon0 = pics.get(hand.hand.get(hand.hand.size() - 1)).icon;
			ImageIcon lastIcon = new ImageIcon();
			lastIcon.setImage(lastIcon0.getImage().getScaledInstance(6*dim, 6*dim/2*3, Image.SCALE_DEFAULT));
			JButton lastS = new JButton();
			lastS.setIcon(lastIcon);
			lastS.setBorder(null);
			lastS.addActionListener(menu.handL);
			pan.add(lastS);
			buttons.put(lastS, hand.hand.size() - 1);
			pan.setVisible(true);
			menu.inHandS.removeAll();
			menu.inHandS.add(pan);
			pan.updateUI();
		}else if(players.size() == 4 && index == north){
			if(hand.hand.size() == 0) {
				((JPanel) menu.inHandN.getComponent(0)).removeAll();
				menu.inHandN.updateUI();
				return;
			}
			ImageIcon lastIcon = pics.get(hand.hand.get(hand.hand.size() - 1)).iconN;
			lastIcon.setImage(lastIcon.getImage().getScaledInstance(6*dim, 6*dim/2*3, Image.SCALE_DEFAULT));
			JButton lastN = new JButton();
			lastN.setIcon(lastIcon);
			lastN.setBorder(null);
			pan.add(lastN);		
			for(int i = hand.hand.size() -2; i >= 0 ; i--) {
				ImageIcon icon = pics.get(hand.hand.get(i)).partIconN;
				icon.setImage(icon.getImage().getScaledInstance(dim, 6*dim/2*3, Image.SCALE_DEFAULT));
				JButton cardN = new JButton();
				cardN.setIcon(icon);
				cardN.setBorder(null);
				pan.add(cardN);
			}			
			pan.setVisible(true);
			menu.inHandN.removeAll();
			menu.inHandN.add(pan);
			pan.updateUI();
		}else if(index == east) {			
			if(hand.hand.size() == 0) {
				((JPanel) menu.inHandE.getComponent(0)).removeAll();
				menu.inHandE.updateUI();
				menu.inHandE.updateUI();
				return;
			}
			ImageIcon lastIcon = pics.get(hand.hand.get(hand.hand.size() - 1)).iconE;
			lastIcon.setImage(lastIcon.getImage().getScaledInstance(6*dim/2*3, 6*dim, Image.SCALE_DEFAULT));
			JButton lastE = new JButton();
			lastE.setIcon(lastIcon);
			lastE.setBorder(null);
			pan.add(lastE);		
			for(int i = hand.hand.size() -2; i >= 0  ; i--) {
				ImageIcon icon = pics.get(hand.hand.get(i)).partIconE;
				icon.setImage(icon.getImage().getScaledInstance(6*dim/2*3, dim, Image.SCALE_DEFAULT));
				JButton cardE = new JButton();
				cardE.setIcon(icon);
				cardE.setBorder(null);
				pan.add(cardE);
			}
			pan.setPreferredSize(new Dimension(dim*6/2*3+2, dim*(hand.hand.size()+10)+2));
			pan.setVisible(true);
			menu.inHandE.removeAll();
			menu.inHandE.add(pan);
			pan.updateUI();
		}else if(index == west) {
			if(hand.hand.size() == 0) {
				((JPanel) menu.inHandW.getComponent(0)).removeAll();
				menu.inHandW.updateUI();
				return;
			}
			for(int i = 0; i < hand.hand.size() -1; i++) {
				ImageIcon icon = pics.get(hand.hand.get(i)).partIconW;
				icon.setImage(icon.getImage().getScaledInstance(6*dim/2*3, dim, Image.SCALE_DEFAULT));
				JButton cardW = new JButton();
				cardW.setIcon(icon);
				cardW.setBorder(null);
				pan.add(cardW);
			}			
			ImageIcon lastIcon = pics.get(hand.hand.get(hand.hand.size() -1)).iconW;
			lastIcon.setImage(lastIcon.getImage().getScaledInstance(6*dim/2*3, 6*dim, Image.SCALE_DEFAULT));
			JButton lastW = new JButton();
			lastW.setIcon(lastIcon);
			lastW.setBorder(null);
			pan.add(lastW);		
			pan.setPreferredSize(new Dimension(dim*6/2*3+2, dim*(hand.hand.size()+10)+2));
			pan.setVisible(true);
			menu.inHandW.removeAll();
			menu.inHandW.add(pan);
			pan.updateUI();
		}
	}

	private BufferedImage toBufferedImage(Image image) {  
        if (image instanceof BufferedImage) {  
             return (BufferedImage)image;  
        }                   
        // 加载所有像素 
        image = new ImageIcon(image).getImage();                    
        BufferedImage bimage = null;  
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
        try {                        
             int transparency = Transparency.OPAQUE;                        
             // 创建buffer图像  
             GraphicsDevice gs = ge.getDefaultScreenDevice();  
             GraphicsConfiguration gc = gs.getDefaultConfiguration();  
             bimage = gc.createCompatibleImage(  
             image.getWidth(null), image.getHeight(null), transparency);  
        } catch (HeadlessException e) {  
              e.printStackTrace(); 
        }                   
        if (bimage == null) {                         
            int type = BufferedImage.TYPE_INT_RGB;  
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);  
        }                   
        // 复制
        Graphics g = bimage.createGraphics();               	
        // 赋值  
        g.drawImage(image, 0, 0, null);  
        g.dispose();                    
        return bimage;
	}

	//顺时针旋转90度（通过交换图像的整数像素RGB 值）
	private BufferedImage rotateClockwise90(BufferedImage bi) {
		int width = bi.getWidth();
		int height = bi.getHeight();
		BufferedImage bufferedImage = new BufferedImage(height, width, bi.getType());
		for (int i = 0; i < width; i++)	for (int j = 0; j < height; j++)
			bufferedImage.setRGB(height - 1 - j, i, bi.getRGB(i, j));
			return bufferedImage;
	}
 
	//逆时针旋转90度（通过交换图像的整数像素RGB 值）
	private BufferedImage rotateCounterclockwise90(BufferedImage bi) {
		int width = bi.getWidth();
		int height = bi.getHeight();
		BufferedImage bufferedImage = new BufferedImage(height, width, bi.getType());
		for (int i = 0; i < width; i++)	for (int j = 0; j < height; j++)
			bufferedImage.setRGB(j, width -1 - i, bi.getRGB(i, j));
			return bufferedImage;
	}
	
	//旋转180度（通过交换图像的整数像素RGB 值）
	private BufferedImage rotate180(BufferedImage bi) {
		int width = bi.getWidth();
		int height = bi.getHeight();
		BufferedImage bufferedImage = new BufferedImage(width,height,bi.getType());
		for (int i = 0; i < width; i++)	for (int j = 0; j < height; j++)
			bufferedImage.setRGB( width - i-1,height-j-1, bi.getRGB(i, j));
			return bufferedImage;
	}
	
	class IconSet{
		public ImageIcon partIcon;
		public ImageIcon icon;
		public ImageIcon partIconE;
		public ImageIcon iconE;
		public ImageIcon partIconW;
		public ImageIcon iconW;
		public ImageIcon partIconN;
		public ImageIcon iconN;		
	}
}
