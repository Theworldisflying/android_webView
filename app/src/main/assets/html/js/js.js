
function actionbtn() {
  console.log("============");
  document.getElementById("thisp").innerHTML = "js content";

//这里是调android原生使用JavascriptInterface声明的函数
    Android.getClient();

}


function  btn2Click() {
    document.getElementById("thisp").innerHTML = "btn2 click";
    document.getElementById("getinputhtml").value = "123";

}

function btnlis(str) {
    document.getElementById("thisp").innerHTML = str
    
}
