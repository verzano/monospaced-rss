package com.verzano.terminalui.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Key {
  public static final String ESCAPED_PREFIX = "\u001b[";
  // Escaped Keys
  public static final String UP_ARROW = ESCAPED_PREFIX + "A";
  public static final String DOWN_ARROW = ESCAPED_PREFIX + "B";
  public static final String RIGHT_ARROW = ESCAPED_PREFIX + "C";
  public static final String LEFT_ARROW = ESCAPED_PREFIX + "D";
  public static final String BACKSPACE = (char)8 + "";
  public static final String TAB = (char)9 + "";
  public static final String ENTER = (char)13 + "";
  public static final String DELETE = (char)127 + "";
  // Letter Keys
  public static final String A_LOWER = "a";
  public static final String B_LOWER = "b";
  public static final String C_LOWER = "c";
  public static final String D_LOWER = "d";
  public static final String E_LOWER = "e";
  public static final String F_LOWER = "f";
  public static final String G_LOWER = "g";
  public static final String H_LOWER = "h";
  public static final String I_LOWER = "i";
  public static final String J_LOWER = "j";
  public static final String K_LOWER = "k";
  public static final String L_LOWER = "l";
  public static final String M_LOWER = "m";
  public static final String N_LOWER = "n";
  public static final String O_LOWER = "o";
  public static final String P_LOWER = "p";
  public static final String Q_LOWER = "q";
  public static final String R_LOWER = "r";
  public static final String S_LOWER = "s";
  public static final String T_LOWER = "t";
  public static final String U_LOWER = "u";
  public static final String V_LOWER = "v";
  public static final String W_LOWER = "w";
  public static final String X_LOWER = "x";
  public static final String Y_LOWER = "y";
  public static final String Z_LOWER = "z";
  public static final String A_UPPER = "A";
  public static final String B_UPPER = "B";
  public static final String C_UPPER = "C";
  public static final String D_UPPER = "D";
  public static final String E_UPPER = "E";
  public static final String F_UPPER = "F";
  public static final String G_UPPER = "G";
  public static final String H_UPPER = "H";
  public static final String I_UPPER = "I";
  public static final String J_UPPER = "J";
  public static final String K_UPPER = "K";
  public static final String L_UPPER = "L";
  public static final String M_UPPER = "M";
  public static final String N_UPPER = "N";
  public static final String O_UPPER = "O";
  public static final String P_UPPER = "P";
  public static final String Q_UPPER = "Q";
  public static final String R_UPPER = "R";
  public static final String S_UPPER = "S";
  public static final String T_UPPER = "T";
  public static final String U_UPPER = "U";
  public static final String V_UPPER = "V";
  public static final String W_UPPER = "W";
  public static final String X_UPPER = "X";
  public static final String Y_UPPER = "Y";
  public static final String Z_UPPER = "Z";
}
