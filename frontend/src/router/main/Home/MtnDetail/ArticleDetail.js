import React, { useCallback, useEffect, useState } from "react";
import Bar from "../Bar";
import "../Home.css";
import Send from "../../../../config/Send";
import { connect } from "react-redux";
import x from "../../../../res/img/x.png";
function ArticleDetail({ userSlice }) {
  const [nickname, setNickname] = useState();
  const [title, setTitle] = useState();
  const [content, setContent] = useState();
  const [createdAt, setcreatedAt] = useState("2022-05-06T13:20:33.548201");
  const [comments, setComments] = useState([]);
  const [commentContent, setCommentContent] = useState();
  const [commentId, setCommentId] = useState();
  const communityId = window.localStorage.getItem("communityId");
  useEffect(() => {
    getArticle();
  }, []);

  function getArticle() {
    Send.get(`/community/${communityId}`)
      .then((res) => {
        console.log(res);
        setNickname(res.data.nickname);
        setTitle(res.data.title);
        setContent(res.data.content);
        setcreatedAt(res.data.createdAt);
        setComments(res.data.comments);
        Send.get(
          `https://k6a306.p.ssafy.io/api/comment/community/${communityId}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + res.data.accessToken,
            },
          }
        ).then((response) => {
          console.log(response);
          setComments(response.data.commentGetListResponseDTOList);
        });
      })
      .catch((e) => {
        console.log(e);
      });
  }

  function createComment() {
    const data = {
      communityId: communityId,
      content: commentContent,
    };
    Send.post(`/comment`, JSON.stringify(data))
      .then((res) => {
        console.log(res);
        if (res.status === 201) {
          getArticle();
          setCommentContent("");
        }
      })
      .catch((e) => {
        console.log(e);
      });
  }

  function deleteComment(commentId) {
    Send.delete(`/comment/community/${commentId}`)
      .then((res) => {
        console.log(res);
        if (res.code === 200) {
          getArticle();
        }
      })
      .catch((e) => {
        console.log(e);
      });
  }

  const onCommentHandler = (event) => {
    setCommentContent(event.currentTarget.value);
  };

  return (
    <>
      {/* 92.5vh */}
      <div
        style={{
          marginLeft: "8.3vw",
          width: "83.3vw",
          marginRight: "8.3vw",
          height: "84vh",
          marginTop: "8.5vh",
        }}
      >
        <div
          id="header"
          style={{
            position: "relative",
            height: "3.5vh",
            fontSize: "1.8vh",
            marginBottom: "3.5vh",
          }}
        >
          <div style={{ fontWeight: "bolder" }}>{nickname}</div>
          <div style={{ color: "#ACACAC", display: "flex" }}>
            <div>
              {createdAt.substr(0, 10) + " " + createdAt.substr(11, 8)}
            </div>
          </div>
        </div>
        <div
          id="contents"
          style={{
            position: "relative",
            height: "30vh",
            marginBottom: "2vh",
          }}
        >
          <div
            id="title"
            style={{
              fontSize: "2.2vh",
              fontWeight: "bold",
              marginBottom: "1.5vh",
            }}
          >
            {title}
          </div>
          <div id="content" style={{ fontSize: "1.8vh" }}>
            {content}
          </div>
        </div>
        <div id="comments">
          {comments == undefined ? (
            <div
              style={{
                position: "relative",
                fontWeight: "regular",
                fontSize: "2vh",
                marginBottom: "1.5vh",
              }}
            >
              ?????? 0???
            </div>
          ) : (
            <div
              style={{
                position: "relative",
                fontWeight: "regular",
                fontSize: "2vh",
                marginBottom: "1.5vh",
              }}
            >
              ?????? {comments.length}???
            </div>
          )}

          <div
            id="comment"
            className="box"
            style={{ height: "30vh", overflow: "auto" }}
          >
            {comments &&
              comments.map((comment) => (
                //   10.5vh
                <div
                  style={{
                    fontSize: "1.7vh",
                    borderBottom: "1px solid #8E8E92",
                  }}
                >
                  <div style={{ display: "flex" }}>
                    <div style={{ fontWeight: "bold", marginTop: "1.5vh" }}>
                      {comment.nickname}
                    </div>
                    {userSlice.userNickname == comment.nickname ? (
                      <>
                        <div
                          style={{
                            color: "#FF7760",
                            fontWeight: "bold",
                            marginTop: "1.5vh",
                            marginLeft: "1vw",
                          }}
                        >
                          me
                        </div>
                        <img
                          onClick={() => deleteComment(comment.commentId)}
                          style={{
                            position: "absolute",
                            right: "8.3vw",
                            marginTop: "2vh",
                            width: "1.5vh",
                            height: "1.5vh",
                            marginLeft: "2vw",
                            float: "right",
                          }}
                          src={x}
                        />
                      </>
                    ) : (
                      <></>
                    )}
                  </div>

                  <div style={{ fontSize: "1.7vh" }}>{comment.content}</div>
                  <div style={{ color: "#ACACAC", marginBottom: "1.5vh" }}>
                    {comment.createdAt.substr(0, 10) + " " + comment.createdAt.substr(11, 8)}
                  </div>
                </div>
              ))}
          </div>
        </div>
        <div>
          <div>
            <input
              className="input"
              value={commentContent}
              onChange={onCommentHandler}
              style={{
                position: "relative",
                background: "#EFEFEF",
                border: "none",
                width: "70.3vw",
                height: "4vh",
                marginTop: "2.7vh",
                paddingLeft: "3vw",
                fontSize: "1.8vh",
                paddingRight: "10vw",
              }}
              placeholder="????????? ??????????????????"
            ></input>
          </div>
          <div>
            <button
              onClick={() => createComment()}
              style={{
                position: "absolute",
                top: "85vh",
                right: "10vw",
                border: "none",
                background: "none",
                color: "#69696C",
                fontSize: "1.8vh",
              }}
            >
              ??????
            </button>
          </div>
        </div>
      </div>
      <Bar></Bar>
    </>
  );
}
function mapStateToProps(state) {
  return { userSlice: state.user };
}

export default connect(mapStateToProps)(ArticleDetail);
