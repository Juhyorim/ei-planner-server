package com.kihyaa.Eiplanner.domain;

/**
 * EIType은 일정의 사분면 타입을 나타냅니다.
 *
 * <ul>
 *     <li><b>PENDING</b>: 대기 중인 일정을 나타냅니다.</li>
 *     <li><b>IMPORTANT_URGENT</b>: 중요하면서 긴급한 일정을 나타냅니다.</li>
 *     <li><b>IMPORTANT_NOT_URGENT</b>: 중요하지만 긴급하지 않은 일정을 나타냅니다.</li>
 *     <li><b>NOT_IMPORTANT_URGENT</b>: 중요하지 않으면서 긴급한 일정을 나타냅니다.</li>
 *     <li><b>NOT_IMPORTANT_NOT_URGENT</b>: 중요하지 않고 긴급하지 않은 일정을 나타냅니다.</li>
 * </ul>
 */
public enum EIType {
    PENDING, // 대기 중
    IMPORTANT_URGENT, // 중요하면서 긴급
    IMPORTANT_NOT_URGENT, // 중요하지만 긴급하지 않음
    NOT_IMPORTANT_URGENT, // 중요하지 않으면서 긴급
    NOT_IMPORTANT_NOT_URGENT // 중요하지 않고 긴급하지 않음
}

