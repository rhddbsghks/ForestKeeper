import axios from "axios";
const url = "";
function checkId(email, url) {
  axios
    .get(url + `/user/email/${email}`)
    .then(function (response) {
      console.log(response.data.valid);
      if (response.data.valid === true) {
        alert("사용 가능한 이메일입니다.");
        return true;
      } else if (response.data.valid === false) {
        alert("이미 존재하는 이메일입니다.");
        return false;
      }
    })
    .catch(function (error) {
      console.log(error);
    });
}

function checkEmail(email) {
  const regExp =
    /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
  if (regExp.test(email) === false) {
    alert("이메일 형식으로 작성하셔야 합니다.");
    return false;
  }
  return true;
}

function checkName(name) {
  if (name.length < 2 || name.length > 10) {
    alert("이름은 한글로 2자 이상 10자 이하여야 합니다.");
  }
}

function checkNickname(nickname, url) {
  if (nickname.length < 2 || nickname.length > 10) {
    alert("닉네임은 2자 이상 10자 이하여야 합니다.");
  } else {
    axios
      .get(url + `/user/nickname/${nickname}`)
      .then(function (response) {
        console.log(response);
        if (response.data.valid === false) {
          alert("이미 존재하는 닉네임입니다.");
          return false;
        } else if (response.data.valid === true) {
          alert("사용 가능한 닉네임입니다.");
          return true;
        }
      })
      .catch(function (error) {
        console.log(error);
      });
  }
}

export { checkId, checkEmail, checkName, checkNickname };
