package jsp;

public class RandomString{
	
	public static String generateRandomString(int length){
//���ͶüƱK�X
	int[] word = new int[length];
	int mod;
	for(int i = 0; i < length; i++){
		mod = (int)((Math.random()*7)%3);
		if(mod ==1){ //�Ʀr
			word[i]=(int)((Math.random()*10) + 48);
		}else if(mod ==2){ //�j�g�^��
			word[i] = (char)((Math.random()*26) + 65);
		}else{ //�p�g�^��
			word[i] = (char)((Math.random()*26) + 97);
		}
	}
	StringBuffer newPassword = new StringBuffer();
	for(int j = 0; j < length; j++){
		newPassword.append((char)word[j]);
	}
	return newPassword.toString();
	//System.out.println(newPassword);
	}
}
