import React, { useEffect, useState, useRef } from "react";

import SearchInput from "./SearchInput";
import axios from "axios";
import { useInView } from "react-intersection-observer";
import Send from "../../../config/Send";
function ResultItem({ result }) {
  return (
    <>
      <div
        style={{
          borderBottom: "1px solid #EFEFEF",
          cursor: "pointer",
        }}
      >
        <div
          style={{
            fontSize: "2vh",
            marginTop: "2vh",
            marginBottom: "0.5vh",
          }}
        >
          {result.name}
        </div>
        <div
          style={{ fontSize: "1.5vh", color: "#8E8E92", marginBottom: "2vh" }}
        >
          {result.address}
        </div>
      </div>
    </>
  );
}

function SearchList({ keyword, keywordHandler }) {
  const url = "https://k6a306.p.ssafy.io/api";
  const [searchList, setSearchList] = useState([]);
  const [pageNumber, setPageNumber] = useState(1);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);

  const getItems = async (pageNumber) => {
    getResult();
  };

  useEffect(() => {
    if (pageNumber != 1 && total != 0) {
      if (total >= pageNumber * 8) {
        getItems(pageNumber);
      }
    } else {
      getItems(pageNumber);
    }
  }, [pageNumber]);

  function getResult() {
    Send.get(url + `/mountain`, {
      params: {
        keyword: keyword,
        page: pageNumber,
      },
    })
      .then(function (response) {
        console.log(response.data);
        //setSearchList(response.data.searchlist);
        setSearchList((list) => [...list, ...response.data.mountainSearchDTOList]);
        setTotal(response.data.total);
        setLoading(true);
      })
      .catch(function (error) {
        console.log(error.code);
      });
  }

  const loadMore = () => {
    setPageNumber((prevPageNumber) => prevPageNumber + 1);
  };

  const pageEnd = useRef();

  useEffect(() => {
    if (loading) {
      const observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            loadMore();
          }
        },
        { threshold: 1 }
      );
      observer.observe(pageEnd.current);
    }
  }, [loading]);

  function goSearch() {
    if (keyword !== "" && keyword != undefined) {
      setPageNumber(1);
      setSearchList([]);
      getResult();
    } else {
      alert("???????????? ??????????????????.");
    }
  }

  const onSubmit = (event) => {
    if (event.key === "Enter") {
      console.log(keyword);
      if (keyword !== "" && keyword !== undefined) {
        setPageNumber(1);
        setSearchList([]);
        getResult();
      } else {
        alert("???????????? ??????????????????.");
      }
    }
  };

  function goDetail(result) {
    document.location.href = `/detail/${result.mountainCode}`;
  }

  return (
    <>
      <SearchInput
        keyword={keyword}
        keywordHandler={keywordHandler}
        onSubmit={onSubmit}
        goSearch={goSearch}
      ></SearchInput>

      <div
        className="box"
        // ref={pageEnd}
        id="List"
        style={{
          height: "81.9vh",
          borderTop: "1px solid #EFEFEF",
          marginLeft: "5vw",
          width: "90vw",
          overflow: "auto",
        }}
      >
        {searchList &&
          searchList.map((result) => (
            <div onClick={() => goDetail(result)}>
              <ResultItem result={result}></ResultItem>
            </div>
          ))}

        <div style={{ height: "2vh" }} ref={pageEnd}></div>
      </div>

      {/* <Bar></Bar> */}
    </>
  );
}
export default SearchList;
